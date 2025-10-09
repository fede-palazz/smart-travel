package com.certimetergroup.smart_travel.bff_api.mapper;


import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.ActivityResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.FlightResDTO;
import java.time.Instant;
import java.util.Set;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import shared.Room;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Named("stringToObjectId")
  static ObjectId mapStringToObjectId(String id) {
    return new ObjectId(id);
  }

  @Mapping(source = "flight.id", target = "flightId", qualifiedByName = "stringToObjectId")
  @Mapping(target = "quantity", expression = "java(quantity)")
  FlightOrder toOrder(FlightResDTO flight, Integer quantity);

  @Mapping(source = "accommodation.id", target = "accommodationId", qualifiedByName = "stringToObjectId")
  @Mapping(target = "startDate", expression = "java(startDate)")
  @Mapping(target = "endDate", expression = "java(endDate)")
  @Mapping(target = "rooms", expression = "java(rooms)")
  AccommodationOrder toOrder(
      AccommodationDetailsResDTO accommodation,
      Instant startDate,
      Instant endDate,
      Set<Room> rooms
  );

  @Mapping(source = "activity.id", target = "activityId", qualifiedByName = "stringToObjectId")
  @Mapping(target = "date", expression = "java(date)")
  @Mapping(target = "quantity", expression = "java(quantity)")
  @Mapping(target = "startTime", source = "activity.schedule.recurrence.startTime")
  @Mapping(target = "endTime", source = "activity.schedule.recurrence.endTime")
  ActivityOrder toOrder(ActivityResDTO activity, Instant date, Integer quantity);


}
