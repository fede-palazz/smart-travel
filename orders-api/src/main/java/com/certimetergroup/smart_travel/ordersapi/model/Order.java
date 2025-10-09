package com.certimetergroup.smart_travel.ordersapi.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import shared.UserSummary;
import shared.order.OrderItems;
import shared.order.OrderType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

  @Id
  private String id;
  private UserSummary customerInfo;
  private Instant createdAt;
  private OrderType type;
  private OrderItems items;
  private Payment payment;
  private String agencyPackageId; // In case of agency package
}
