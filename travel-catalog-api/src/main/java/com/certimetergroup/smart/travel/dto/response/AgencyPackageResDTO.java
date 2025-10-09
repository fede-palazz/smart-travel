package com.certimetergroup.smart.travel.dto.response;


import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import shared.DestinationSummary;
import shared.PackageStatus;
import shared.Price;
import shared.UserSummary;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyPackageResDTO {

  private ObjectId id;
  private String name;
  private String description;
  private Set<String> tags;
  private PackageStatus status;
  private Instant startDate;
  private Instant endDate;
  private Price totalPrice;
  private DestinationSummary destination;
  private String mainPicture;
  private Set<String> pictures;
  private FlightOrder departureFlight;
  private FlightOrder returnFlight;
  private AccommodationOrder accommodation;
  private Set<ActivityOrder> activities;
  private UserSummary agentInfo;
  private Instant creationDate;
  private Integer quantity;
}
