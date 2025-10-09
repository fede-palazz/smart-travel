package com.certimetergroup.smart_travel.ordersapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentReqDTO {

  private String orderId;
  private String description;
  private String customerId;
  private Double amount;
}
