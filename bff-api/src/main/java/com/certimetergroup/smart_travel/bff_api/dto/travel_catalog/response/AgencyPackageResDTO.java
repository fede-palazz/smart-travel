package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;


import java.time.Instant;
import java.util.Set;
import org.bson.types.ObjectId;
import shared.DestinationSummary;
import shared.PackageStatus;
import shared.Price;
import shared.UserSummary;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

public record AgencyPackageResDTO(
    ObjectId id,
    String name,
    String description,
    Set<String> tags,
    PackageStatus status,
    Instant startDate,
    Instant endDate,
    Price totalPrice,
    DestinationSummary destination,
    String mainPicture,
    Set<String> pictures,
    FlightOrder departureFlight,
    FlightOrder returnFlight,
    AccommodationOrder accommodation,
    Set<ActivityOrder> activities,
    UserSummary agentInfo,
    Instant creationDate,
    Integer quantity
) {

}
