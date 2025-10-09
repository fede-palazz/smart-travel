package com.certimetergroup.smart.travel.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.FlightDestination;
import shared.Price;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightReqDTO {

  @NotBlank(message = "Parameter 'code' is required")
  private String code;

  @NotNull(message = "Parameter 'capacity' is required")
  @Positive(message = "Parameter 'capacity' must be a positive number")
  private Integer capacity;

  @NotBlank(message = "Parameter 'airline' is required")
  private String airline;

  @NotBlank(message = "Parameter 'airlineLogo' is required")
  private String airlineLogo;

  @NotNull(message = "Parameter 'from' is required")
  @Valid
  private FlightDestination from;

  @NotNull(message = "Parameter 'to' is required")
  @Valid
  private FlightDestination to;

  @NotNull(message = "Parameter 'departureTime' is required")
  private Instant departureTime;

  @NotNull(message = "Parameter 'arrivalTime' is required")
  private Instant arrivalTime;

  @NotNull(message = "Parameter 'price' is required")
  @Valid
  private Price price;
}
