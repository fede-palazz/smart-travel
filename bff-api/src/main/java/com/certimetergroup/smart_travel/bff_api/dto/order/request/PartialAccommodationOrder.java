package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import shared.Room;

@Data
@SuperBuilder
@AllArgsConstructor
public class PartialAccommodationOrder {

  @NotBlank(message = "Parameter 'accommodationId' is required")
  @Size(min = 24, max = 24, message = "Parameter 'accommodationId' is invalid")
  public String accommodationId;

  @NotNull(message = "Parameter 'rooms' is required")
  @Valid
  public Set<Room> rooms;

  @NotNull(message = "Parameter 'startDate' is required")
  public Instant startDate;

  @NotNull(message = "Parameter 'endDate' is required")
  public Instant endDate;
}
