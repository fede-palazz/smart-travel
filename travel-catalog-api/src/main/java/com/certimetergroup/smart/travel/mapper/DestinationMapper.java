package com.certimetergroup.smart.travel.mapper;

import com.certimetergroup.smart.travel.dto.request.DestinationReqDTO;
import com.certimetergroup.smart.travel.dto.response.DestinationResDTO;
import com.certimetergroup.smart.travel.model.Destination;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface DestinationMapper {

  Destination toEntity(DestinationReqDTO destinationReqDTO);

  DestinationResDTO toDto(Destination destination);
}
