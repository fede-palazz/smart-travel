package com.certimetergroup.smart.travel.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import shared.FlightDestination;
import shared.Price;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "flights")
public class Flight extends ReactivePanacheMongoEntity {

  public String code;
  public Integer capacity;
  public String airline;
  public String airlineLogo;
  public FlightDestination from;
  public FlightDestination to;
  public Instant departureTime;
  public Instant arrivalTime;
  public Price price;

  public static String generateRandomFlightCode(String airlineCode) {
    int flightNumber = ThreadLocalRandom.current().nextInt(100, 9999); // 3 to 4 digit number
    return airlineCode + flightNumber;
  }

}
