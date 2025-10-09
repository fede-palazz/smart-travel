package com.certimetergroup.smart_travel.ordersapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.Price;
import shared.order.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  private PaymentStatus status;
  private Instant paidAt;
  private Price amount;
  @JsonIgnore
  private String token;
  @JsonIgnore
  private String payerId;

}
