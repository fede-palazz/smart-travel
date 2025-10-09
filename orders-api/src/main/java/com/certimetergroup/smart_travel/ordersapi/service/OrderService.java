package com.certimetergroup.smart_travel.ordersapi.service;


import com.certimetergroup.smart_travel.ordersapi.dto.request.CancelOrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.request.CaptureOrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.request.OrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.OrderResDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.PagedResDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.PaymentUrlResDTO;
import com.certimetergroup.smart_travel.ordersapi.filter.OrderFilter;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import shared.order.PaymentStatus;

public interface OrderService {

  Mono<PagedResDTO<OrderResDTO>> getOrders(
      int page,
      int size,
      String sort,
      String order,
      OrderFilter filters
  );

  Mono<OrderResDTO> getOrderById(String id);

  Mono<OrderResDTO> getOrderByToken(String token);

  Mono<PaymentUrlResDTO> createOrder(@Valid OrderReqDTO orderReqDTO);

  Mono<Void> deleteOrder(String id);

  Mono<Void> captureOrder(@Valid CaptureOrderReqDTO captureReq);

  Mono<Void> cancelOrder(@Valid CancelOrderReqDTO cancelReq);

  Mono<Void> setCancelled(String token);

  Mono<Void> setConfirmed(String token);

  Mono<Void> setExpired(String token);

  Mono<PaymentStatus> getPaymentStatus(String token);
}
