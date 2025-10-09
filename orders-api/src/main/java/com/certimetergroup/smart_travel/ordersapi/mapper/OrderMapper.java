package com.certimetergroup.smart_travel.ordersapi.mapper;


import com.certimetergroup.smart_travel.ordersapi.dto.request.OrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.OrderNotificationDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.OrderResDTO;
import com.certimetergroup.smart_travel.ordersapi.model.Order;
import com.certimetergroup.smart_travel.ordersapi.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import shared.order.PaymentStatus;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  OrderResDTO toDto(Order order);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
  @Mapping(target = "payment", expression = "java(createInitialPayment(orderReqDTO, token))")
  Order toEntity(OrderReqDTO orderReqDTO, String token);

  // Helper method to create the initial Payment object
  default Payment createInitialPayment(OrderReqDTO dto, String token) {
    return Payment.builder()
        .amount(dto.getAmount())
        .status(PaymentStatus.PENDING)
        .paidAt(null)
        .payerId(null)
        .token(token)
        .build();
  }
  
  @Mapping(source = "payment.amount", target = "amount")
  OrderNotificationDTO toDTO(OrderResDTO orderResDTO);
}
