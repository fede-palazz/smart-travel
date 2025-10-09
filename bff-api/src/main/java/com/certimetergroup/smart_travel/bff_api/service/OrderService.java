package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialAgencyOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.response.OrderResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.response.PaymentUrlResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.OrderFilter;
import reactor.core.publisher.Mono;

public interface OrderService {

  Mono<PagedResDTO<OrderResDTO>> getOrders(
      Integer page,
      Integer size,
      String sort,
      String order,
      OrderFilter filters
  );

  Mono<OrderResDTO> getOrderById(String id);

  Mono<PaymentUrlResDTO> createOrder(
      PartialOrderReqDTO partialOrderReq,
      String userId
  );

  Mono<PaymentUrlResDTO> createAgencyOrder(
      PartialAgencyOrderReqDTO partialAgencyOrderReq,
      String userId
  );

  Mono<Void> captureOrder(String token, String payerId);

  Mono<Void> cancelOrder(String token);
}
