package com.certimetergroup.smart.travel.mapper;

import com.certimetergroup.smart.travel.dto.request.AgencyPackageReqDTO;
import com.certimetergroup.smart.travel.dto.response.AgencyPackageResDTO;
import com.certimetergroup.smart.travel.model.AgencyPackage;
import java.time.Instant;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import shared.PackageStatus;

@Mapper(componentModel = "cdi")
public interface AgencyPackageMapper {

  AgencyPackage toEntity(AgencyPackageReqDTO agencyPackageReqDTO);

  @AfterMapping
  default void setDefaultFields(@MappingTarget AgencyPackage agencyPackage) {
    agencyPackage.status = PackageStatus.DRAFT;
    agencyPackage.creationDate = Instant.now();
  }

  AgencyPackageResDTO toDto(AgencyPackage agencyPackage);
}
