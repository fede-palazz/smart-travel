package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.Coordinates;
import shared.Country;

@Data
@NoArgsConstructor
public class DestinationReqDTO {

  private String city;
  private String region;
  private Country country;
  private Coordinates coordinates;
  private String description;
  private Set<String> pictures;
  private Set<String> tags;
  private Integer popularityScore;
  private String timezone;
}

