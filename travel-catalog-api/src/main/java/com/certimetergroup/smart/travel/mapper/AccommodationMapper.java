package com.certimetergroup.smart.travel.mapper;

import com.certimetergroup.smart.travel.dto.request.AccommodationReqDTO;
import com.certimetergroup.smart.travel.dto.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart.travel.dto.response.AccommodationResDTO;
import com.certimetergroup.smart.travel.model.Accommodation;
import com.certimetergroup.smart.travel.model.AccommodationDetails;
import com.certimetergroup.smart.travel.utils.GeoUtils;
import java.util.HashSet;
import java.util.OptionalDouble;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import shared.Price;
import shared.ReviewsSummary;
import shared.Room;

@Mapper(componentModel = "cdi")
public interface AccommodationMapper {

  AccommodationDetails toDetailsEntity(AccommodationReqDTO accommodationReqDTO);

  @AfterMapping
  default void initAccommodationDetails(@MappingTarget AccommodationDetails details) {
    // Calculate distance to center
    if (details.destination != null && details.coordinates != null &&
        details.destination.coordinates != null) {
      details.distanceToCenterKm = GeoUtils.haversineDistance(
          details.coordinates,
          details.destination.coordinates
      );
    }
    // Set default values
    details.description = "";
    details.details = "";
    details.pictures = new HashSet<>();
    details.services = new HashSet<>();
    details.languages = new HashSet<>();
    details.rooms = new HashSet<>();
    // Set default reviews summary
    details.reviewsSummary = ReviewsSummary.builder()
        .totalCount(0L)
        .avgRating(0.0)
        .build();
  }

  AccommodationResDTO toDto(Accommodation accommodation);

  AccommodationResDTO toDto(AccommodationDetails details);

  @AfterMapping
  default void setMinRoomPrice(AccommodationDetails source,
      @MappingTarget AccommodationResDTO target) {
    if (source.rooms != null && !source.rooms.isEmpty()) {
      OptionalDouble min = source.rooms.stream()
          .map(Room::getPricePerNight)
          .mapToDouble(Price::getValue)
          .min();
      target.setPricePerNight(min.orElse(0.0));
    }
  }

  AccommodationDetailsResDTO toDetailsDto(AccommodationDetails accommodationDetails);
}
