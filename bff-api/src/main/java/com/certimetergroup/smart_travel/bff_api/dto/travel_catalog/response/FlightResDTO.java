package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;


import java.time.Instant;
import shared.FlightDestination;
import shared.Price;

public record FlightResDTO(
    String id,
    String code,
    Integer capacity,
    String airline,
    String airlineLogo,
    FlightDestination from,
    FlightDestination to,
    Instant departureTime,
    Instant arrivalTime,
    Price price
) {

}
