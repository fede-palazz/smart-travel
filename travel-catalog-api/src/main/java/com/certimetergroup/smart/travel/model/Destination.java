package com.certimetergroup.smart.travel.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import shared.Coordinates;
import shared.Country;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "destinations")
public class Destination extends ReactivePanacheMongoEntity {

  public String city;
  public String region;
  public Country country;
  public Coordinates coordinates;
  public String description;
  public Set<String> pictures;
  public Set<String> tags;
  public Integer popularityScore;
  public String timezone;
}
