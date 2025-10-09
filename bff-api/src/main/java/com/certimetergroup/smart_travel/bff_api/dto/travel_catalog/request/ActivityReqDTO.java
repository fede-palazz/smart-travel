package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Price;
import shared.Schedule;

@Data
@NoArgsConstructor
public class ActivityReqDTO {

  private String name;
  private String type;
  private String description;
  private String notes;
  private String address;
  private Coordinates coordinates;
  private DestinationSummary destination;
  private String mainPicture;
  private Set<String> pictures;
  private Set<String> tags;
  private Set<String> languages;
  private Schedule schedule;
  private Price price;
}
