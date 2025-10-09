package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.FlightReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.FlightResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.FlightFilter;
import reactor.core.publisher.Mono;

public interface FlightService {

  Mono<PagedResDTO<FlightResDTO>> getFlights(Integer page, Integer size, String sort, String order,
      String timezone, FlightFilter filters);

  Mono<FlightResDTO> getFlightById(String id);

  Mono<FlightResDTO> addFlight(FlightReqDTO flightReq);

  Mono<FlightResDTO> updateFlight(String id, FlightReqDTO flightReq);

  Mono<OkResDTO> deleteFlight(String id);
}
