package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class PartialFlightOrder {

  @NotBlank(message = "Parameter 'flightId' is required")
  @Size(min = 24, max = 24, message = "Parameter 'flightId' is invalid")
  public String flightId;

  @NotNull(message = "Parameter 'quantity' is required")
  @Positive(message = "Parameter 'quantity' must represent a positive integer")
  public Integer quantity;
}
