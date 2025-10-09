package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.DestinationReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.DestinationResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.DestinationSearchResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.DestinationFilter;
import java.util.List;
import reactor.core.publisher.Mono;

public interface DestinationService {

  Mono<PagedResDTO<DestinationResDTO>> getDestinations(Integer page, Integer size, String sort,
      String order, DestinationFilter filters);

  Mono<DestinationResDTO> getDestinationById(String id);

  Mono<List<DestinationSearchResDTO>> getDestinationsList(String name);

  Mono<DestinationResDTO> addDestination(DestinationReqDTO destinationReq);

  Mono<DestinationResDTO> updateDestination(String id, DestinationReqDTO destinationReq);

  Mono<OkResDTO> deleteDestination(String id);
}
