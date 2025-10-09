package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.FlightReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.FlightResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.FlightFilter;
import com.certimetergroup.smart_travel.bff_api.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FlightController {

  private final FlightService flightService;

  @QueryMapping("getFlights")
  public Mono<PagedResDTO<FlightResDTO>> getFlights(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument String timezone,
      @Argument FlightFilter filters
  ) {
    return flightService.getFlights(page, size, sort, order, timezone, filters);
  }

  @QueryMapping("getFlightById")
  public Mono<FlightResDTO> flightById(@Argument String id) {
    return flightService.getFlightById(id);
  }

  @MutationMapping("addFlight")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<FlightResDTO> addFlight(@Argument FlightReqDTO flightReq) {
    return flightService.addFlight(flightReq);
  }

  @MutationMapping("updateFlight")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<FlightResDTO> updateFlight(@Argument String id, @Argument FlightReqDTO flightReq) {
    return flightService.updateFlight(id, flightReq);
  }

  @MutationMapping("deleteFlight")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<OkResDTO> deleteFlight(@Argument String id) {
    return flightService.deleteFlight(id);
  }
}
