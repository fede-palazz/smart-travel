package com.certimetergroup.smart.travel.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import shared.Contacts;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Policies;
import shared.ReviewsSummary;
import shared.Room;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "accommodations")
public class AccommodationDetails extends ReactivePanacheMongoEntity {

  public String name;
  public String type;                         // "hotel", "b&b", "apartment", "resort"
  public Coordinates coordinates;
  public DestinationSummary destination;
  public String address;
  public Double distanceToCenterKm;
  public String mainPicture;
  public String description;
  public String details;
  public String checkInTime;
  public String checkOutTime;
  public Contacts contacts;
  public Policies policies;
  public Set<String> pictures;
  public Set<String> services;                // "Wi-Fi", "parking", "breakfast"
  public Set<String> languages;
  public Set<Room> rooms;
  public ReviewsSummary reviewsSummary;
}
