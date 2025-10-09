package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request;


import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.Coordinates;
import shared.DestinationSummary;

@Data
@NoArgsConstructor
public class AccommodationReqDTO {

  private String name;
  private String type;
  private Set<String> services;
  private DestinationSummary destination;
  private String address;
  private Coordinates coordinates;
  private String mainPicture;
}
