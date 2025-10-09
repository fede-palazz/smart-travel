package com.certimetergroup.smart.travel.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Price;
import shared.ReviewsSummary;
import shared.Schedule;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "activities")
public class Activity extends ReactivePanacheMongoEntity {

  public String name;
  public String type;                       // guided_tour, museum
  public String description;
  public String notes;
  public String address;                    // meeting point
  public Coordinates coordinates;
  public DestinationSummary destination;    // city where activity is located
  public String mainPicture;
  public Set<String> pictures;
  public Set<String> tags;                  // family, food, nature
  public Set<String> languages;
  public Schedule schedule;
  public Price price;
  public ReviewsSummary reviewsSummary;
}
