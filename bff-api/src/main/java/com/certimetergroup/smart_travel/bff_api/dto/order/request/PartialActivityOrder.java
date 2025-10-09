package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class PartialActivityOrder {

  @NotBlank(message = "Parameter 'activityId' is required")
  @Size(min = 24, max = 24, message = "Parameter 'activityId' is invalid")
  private String activityId;

  @NotNull(message = "Parameter 'date' is required")
  private Instant date;

  @NotNull(message = "Parameter 'quantity' is required")
  @Positive(message = "Parameter 'quantity' must represent a positive integer")
  private Integer quantity;
}
