package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;


import java.util.Set;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.ReviewsSummary;

public record AccommodationResDTO(
    String id,
    String name,
    String type,
    Set<String> services,
    DestinationSummary destination,
    String address,
    Coordinates coordinates,
    Double distanceToCenterKm,
    String mainPicture,
    Double pricePerNight,
    ReviewsSummary reviewsSummary
) {

}
