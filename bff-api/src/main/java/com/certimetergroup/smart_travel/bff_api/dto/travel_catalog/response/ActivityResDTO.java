package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;


import java.util.Set;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Price;
import shared.ReviewsSummary;
import shared.Schedule;

public record ActivityResDTO(
    String id,
    String name,
    String type,
    String description,
    String notes,
    String address,
    Coordinates coordinates,
    DestinationSummary destination,
    String mainPicture,
    Set<String> pictures,
    Set<String> tags,
    Set<String> languages,
    Schedule schedule,
    Price price,
    ReviewsSummary reviewsSummary
) {

}
