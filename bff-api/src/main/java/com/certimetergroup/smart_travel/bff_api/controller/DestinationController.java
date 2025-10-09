package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.DestinationReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.DestinationResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.DestinationSearchResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.DestinationFilter;
import com.certimetergroup.smart_travel.bff_api.service.DestinationService;
import java.util.List;
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
public class DestinationController {

  private final DestinationService destinationService;

  @QueryMapping("getDestinations")
  public Mono<PagedResDTO<DestinationResDTO>> getDestinations(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument DestinationFilter filters
  ) {
    return destinationService.getDestinations(page, size, sort, order, filters);
  }

  @QueryMapping("getDestinationById")
  public Mono<DestinationResDTO> getDestinationById(@Argument String id) {
    return destinationService.getDestinationById(id);
  }

  @QueryMapping("getDestinationsList")
  public Mono<List<DestinationSearchResDTO>> getDestinationsList(@Argument String name) {
    return destinationService.getDestinationsList(name);
  }

  @MutationMapping("addDestination")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<DestinationResDTO> addDestination(@Argument DestinationReqDTO destinationReq) {
    return destinationService.addDestination(destinationReq);
  }

  @MutationMapping("updateDestination")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<DestinationResDTO> updateDestination(@Argument String id,
      @Argument DestinationReqDTO destinationReq) {
    return destinationService.updateDestination(id, destinationReq);
  }

  @MutationMapping("deleteDestination")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<OkResDTO> deleteDestination(@Argument String id) {
    return destinationService.deleteDestination(id);
  }

}
