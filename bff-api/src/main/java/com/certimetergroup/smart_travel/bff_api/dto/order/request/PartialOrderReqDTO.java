package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import jakarta.validation.Valid;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class PartialOrderReqDTO {

  @Valid
  private PartialFlightOrder departureFlight;

  @Valid
  private PartialFlightOrder returnFlight;

  @Valid
  private PartialAccommodationOrder accommodation;

  @Valid
  private HashSet<PartialActivityOrder> activities;
}
