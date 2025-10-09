package com.certimetergroup.smart.travel.dto.response;


import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import shared.FlightDestination;
import shared.Price;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightResDTO {

  private ObjectId id;
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
