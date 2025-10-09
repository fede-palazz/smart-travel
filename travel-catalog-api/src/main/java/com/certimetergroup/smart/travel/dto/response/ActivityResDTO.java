package com.certimetergroup.smart.travel.dto.response;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Price;
import shared.ReviewsSummary;
import shared.Schedule;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResDTO {

  private ObjectId id;
  private String name;
  private String type;
  private String description;
  private String notes;
  private String address;
  private Coordinates coordinates;
  private DestinationSummary destination;
  private String mainPicture;
  private Set<String> pictures = new HashSet<>();
  private Set<String> tags = new HashSet<>();
  private Set<String> languages = new HashSet<>();
  private Schedule schedule;
  private Price price;
  private ReviewsSummary reviewsSummary;
}
