package com.certimetergroup.smart.travel.dto.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import shared.Contacts;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Policies;
import shared.ReviewsSummary;
import shared.Room;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDetailsResDTO {

  private ObjectId id;
  private String name;
  private String type;                         // "hotel", "b&b", "apartment", "resort"
  private String address;
  private Coordinates coordinates;
  private DestinationSummary destination;
  private Double distanceToCenterKm;
  private String mainPicture;
  private String description;
  private String details;
  private String checkInTime;
  private String checkOutTime;
  private Contacts contacts;
  private Policies policies;
  private Set<String> pictures;
  private Set<String> services;                // "Wi-Fi", "parking", "breakfast"
  private Set<String> languages;
  private Set<Room> rooms;
  private ReviewsSummary reviewsSummary;
}
