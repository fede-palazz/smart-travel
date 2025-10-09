package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AccommodationReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AccommodationUpdateReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.AccommodationFilter;
import com.certimetergroup.smart_travel.bff_api.service.AccommodationService;
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
public class AccommodationController {

  private final AccommodationService accommodationService;

  @QueryMapping("getAccommodations")
  public Mono<PagedResDTO<AccommodationResDTO>> accommodations(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument String timezone,
      @Argument AccommodationFilter filters
  ) {
    return accommodationService.getAccommodations(page, size, sort, order, timezone, filters);
  }

  @QueryMapping("getAccommodationById")
  public Mono<AccommodationResDTO> getAccommodationById(@Argument String id) {
    return accommodationService.getAccommodationById(id);
  }

  @QueryMapping("getAccommodationDetailsById")
  public Mono<AccommodationDetailsResDTO> getAccommodationDetailsById(@Argument String id) {
    return accommodationService.getAccommodationDetailsById(id);
  }

  @MutationMapping("addAccommodation")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<AccommodationResDTO> addAccommodation(
      @Argument AccommodationReqDTO accommodationReq) {
    return accommodationService.addAccommodation(accommodationReq);
  }

  @MutationMapping("updateAccommodation")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<AccommodationDetailsResDTO> updateAccommodation(@Argument String id,
      @Argument AccommodationUpdateReqDTO accommodationReq) {
    return accommodationService.updateAccommodation(id, accommodationReq);
  }

  @MutationMapping("deleteAccommodation")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<OkResDTO> deleteAccommodation(@Argument String id) {
    return accommodationService.deleteAccommodation(id);
  }

}
