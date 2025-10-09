package com.certimetergroup.smart.travel.mapper;

import com.certimetergroup.smart.travel.dto.request.FlightReqDTO;
import com.certimetergroup.smart.travel.dto.response.FlightResDTO;
import com.certimetergroup.smart.travel.model.Flight;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface FlightMapper {

  Flight toEntity(FlightReqDTO flightReqDTO);

  FlightResDTO toDto(Flight flight);
}
