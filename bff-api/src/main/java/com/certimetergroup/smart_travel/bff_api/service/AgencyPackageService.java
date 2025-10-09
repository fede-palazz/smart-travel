package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AgencyPackageReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AgencyPackageResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.AgencyPackageFilter;
import reactor.core.publisher.Mono;

public interface AgencyPackageService {

  Mono<PagedResDTO<AgencyPackageResDTO>> getAgencyPackages(Integer page, Integer size, String sort,
      String order, AgencyPackageFilter filters);

  Mono<AgencyPackageResDTO> getAgencyPackageById(String id);

  Mono<AgencyPackageResDTO> addAgencyPackage(AgencyPackageReqDTO packageReq);

  Mono<AgencyPackageResDTO> updateAgencyPackage(String id, AgencyPackageReqDTO packageReq);

  Mono<OkResDTO> deleteAgencyPackage(String id);

  Mono<OkResDTO> publishAgencyPackage(String id);

  Mono<OkResDTO> archiveAgencyPackage(String id);

}
