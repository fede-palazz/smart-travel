package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;


import java.util.Set;
import shared.Contacts;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Policies;
import shared.ReviewsSummary;
import shared.Room;


public record AccommodationDetailsResDTO(
    String id,
    String name,
    String type,
    String address,
    Coordinates coordinates,
    DestinationSummary destination,
    Double distanceToCenterKm,
    String mainPicture,
    String description,
    String details,
    String checkInTime,
    String checkOutTime,
    Contacts contacts,
    Policies policies,
    Set<String> pictures,
    Set<String> services,
    Set<String> languages,
    Set<Room> rooms,
    ReviewsSummary reviewsSummary
) {

}
