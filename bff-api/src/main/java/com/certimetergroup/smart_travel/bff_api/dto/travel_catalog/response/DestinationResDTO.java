package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;


import java.util.Set;
import shared.Coordinates;
import shared.Country;

public record DestinationResDTO(
    String id,
    String city,
    String region,
    Country country,
    Coordinates coordinates,
    String description,
    Set<String> pictures,
    Set<String> tags,
    Integer popularityScore,
    String timezone
) {

}
