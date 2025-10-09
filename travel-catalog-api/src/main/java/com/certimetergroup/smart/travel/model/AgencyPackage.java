package com.certimetergroup.smart.travel.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import shared.DestinationSummary;
import shared.PackageStatus;
import shared.Price;
import shared.UserSummary;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "agency_packages")
public class AgencyPackage extends ReactivePanacheMongoEntity {

  public String name;
  public String description;
  public Set<String> tags;
  public PackageStatus status;
  public Instant startDate;
  public Instant endDate;
  public Price totalPrice;
  public DestinationSummary destination;
  public String mainPicture;
  public Set<String> pictures;
  public FlightOrder departureFlight;
  public FlightOrder returnFlight;
  public AccommodationOrder accommodation;
  public Set<ActivityOrder> activities;
  public UserSummary agentInfo;
  public Instant creationDate;
  public Integer quantity;    // Package target number of people
}
