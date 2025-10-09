package com.certimetergroup.smart_travel.ordersapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import shared.Price;
import shared.UserSummary;
import shared.order.OrderItems;
import shared.order.OrderType;

@Data
@AllArgsConstructor
public class OrderReqDTO {

  @NotNull(message = "Parameter 'customerInfo' is required")
  @Valid
  private UserSummary customerInfo;

  @NotNull(message = "Parameter 'type' is required")
  private OrderType type;

  @NotNull(message = "Parameter 'items' is required")
  @Valid
  private OrderItems items;

  @NotNull(message = "Parameter 'amount' is required")
  @Valid
  private Price amount;

  @Size(min = 24, max = 24, message = "Parameter 'agencyPackageId' is invalid")
  private String agencyPackageId; // In case of agency package

}
