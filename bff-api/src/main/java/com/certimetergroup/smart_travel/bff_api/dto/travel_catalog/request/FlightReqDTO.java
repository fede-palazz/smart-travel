package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request;


import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.FlightDestination;
import shared.Price;

@Data
@NoArgsConstructor
public class FlightReqDTO {

  private String code;
  private Integer capacity;
  private String airline;
  private String airlineLogo;
  private FlightDestination from;
  private FlightDestination to;
  private Instant departureTime;
  private Instant arrivalTime;
  private Price price;
}
