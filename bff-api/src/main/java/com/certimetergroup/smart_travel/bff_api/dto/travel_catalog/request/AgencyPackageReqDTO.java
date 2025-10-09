package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request;


import java.time.Instant;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.DestinationSummary;
import shared.Price;
import shared.UserSummary;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

@Data
@NoArgsConstructor
public class AgencyPackageReqDTO {

  private String name;
  private String description;
  private Set<String> tags;
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
  private Integer quantity;
}
