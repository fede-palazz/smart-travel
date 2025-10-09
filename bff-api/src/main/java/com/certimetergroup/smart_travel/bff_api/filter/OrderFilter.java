package com.certimetergroup.smart_travel.bff_api.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import shared.order.OrderType;
import shared.order.PaymentStatus;

@Data
@NoArgsConstructor
public class OrderFilter {

  private String orderId;
  private String customerId;
  private String customerName; // Either firstname or surname or both
  private Double minAmount;
  private Double maxAmount;
  private PaymentStatus status;
  private OrderType type;
}
