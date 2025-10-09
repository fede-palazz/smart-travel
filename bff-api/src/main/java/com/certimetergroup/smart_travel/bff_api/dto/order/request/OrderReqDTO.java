package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import shared.Price;
import shared.UserSummary;
import shared.order.OrderItems;
import shared.order.OrderType;

@Data
@SuperBuilder
@AllArgsConstructor
public class OrderReqDTO {

  private UserSummary customerInfo;
  private OrderType type;
  private OrderItems items;
  private Price amount;
  private String agencyPackageId; // In case of agency package
}
