package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.auth.CustomUserDetails;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialAgencyOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.response.OrderResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.response.PaymentUrlResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import com.certimetergroup.smart_travel.bff_api.filter.OrderFilter;
import com.certimetergroup.smart_travel.bff_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import shared.UserRoleEnum;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @QueryMapping("getOrders")
  @PreAuthorize("isAuthenticated()")
  public Mono<PagedResDTO<OrderResDTO>> getOrders(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument OrderFilter filters,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    if (user.hasRole(UserRoleEnum.CUSTOMER)) {
      // Restrict visualization to logged-in user's orders
      filters.setCustomerId(user.getId());
    }
    return orderService.getOrders(page, size, sort, order, filters);
  }

  @QueryMapping("getOrderById")
  @PreAuthorize("isAuthenticated()")
  public Mono<OrderResDTO> getOrderById(
      @Argument String id,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    return orderService.getOrderById(id).flatMap(order -> {
      // Check if requested order belongs to the user
      if (user.hasRole(UserRoleEnum.CUSTOMER) &&
          !order.customerInfo().getUserId().toHexString().equals(user.getId())) {
        return Mono.error(new GraphqlFailureException(
            new FailureException(ResponseEnum.FORBIDDEN,
                "You are not allowed to access this resource."
            ))
        );
      }
      return Mono.just(order);
    });
  }

  @MutationMapping("createOrder")
  @PreAuthorize("hasRole('CUSTOMER')")
  public Mono<PaymentUrlResDTO> createOrder(
      @Argument PartialOrderReqDTO partialOrderReq,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    return orderService.createOrder(partialOrderReq, userDetails.getId());
  }

  @MutationMapping("createAgencyOrder")
  @PreAuthorize("hasRole('CUSTOMER')")
  public Mono<PaymentUrlResDTO> createAgencyOrder(
      @Argument PartialAgencyOrderReqDTO partialAgencyOrderReq,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    return orderService.createAgencyOrder(partialAgencyOrderReq, userDetails.getId());
  }

}
