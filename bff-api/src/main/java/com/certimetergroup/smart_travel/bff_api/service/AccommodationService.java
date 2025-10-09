package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AccommodationReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AccommodationUpdateReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.AccommodationFilter;
import reactor.core.publisher.Mono;

public interface AccommodationService {

  Mono<PagedResDTO<AccommodationResDTO>> getAccommodations(Integer page, Integer size, String sort,
      String order, String timezone, AccommodationFilter filters);

  Mono<AccommodationResDTO> getAccommodationById(String id);

  Mono<AccommodationDetailsResDTO> getAccommodationDetailsById(String id);

  Mono<AccommodationResDTO> addAccommodation(AccommodationReqDTO accommodationReq);

  Mono<AccommodationDetailsResDTO> updateAccommodation(String id,
      AccommodationUpdateReqDTO accommodationReq);

  Mono<OkResDTO> deleteAccommodation(String id);
}
