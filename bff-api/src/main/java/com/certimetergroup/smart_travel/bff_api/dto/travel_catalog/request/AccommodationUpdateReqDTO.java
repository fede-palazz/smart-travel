package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request;


import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.Contacts;
import shared.Coordinates;
import shared.Policies;
import shared.Room;

@Data
@NoArgsConstructor
public class AccommodationUpdateReqDTO {

  private String name;
  private String type;
  private Set<String> services;
  private String address;
  private Coordinates coordinates;
  private String mainPicture;
  private String description;
  private String details;
  private String checkInTime;
  private String checkOutTime;
  private Set<String> pictures;
  private Contacts contacts;
  private Policies policies;
  private Set<String> languages;
  private Set<Room> rooms;
}
