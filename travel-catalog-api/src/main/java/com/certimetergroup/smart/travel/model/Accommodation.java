package com.certimetergroup.smart.travel.model;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.ReviewsSummary;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ProjectionFor(AccommodationDetails.class)
public class Accommodation {

  public ObjectId id;
  public String name;
  public String type;                         // "hotel", "b&b", "apartment", "resort"
  public Set<String> services;                // "Wi-Fi", "parking", "breakfast"
  public DestinationSummary destination;      // it can be seen as the related city
  public String address;
  public Coordinates coordinates;
  public Double distanceToCenterKm;
  public String mainPicture;
  public ReviewsSummary reviewsSummary;
}
