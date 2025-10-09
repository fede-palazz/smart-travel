package com.certimetergroup.smart.travel.dto.response;


import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.ReviewsSummary;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationResDTO {

  private ObjectId id;
  private String name;
  private String type;
  private Set<String> services;
  private DestinationSummary destination;
  private String address;
  private Coordinates coordinates;
  private Double distanceToCenterKm;
  private String mainPicture;
  private Double pricePerNight;
  private ReviewsSummary reviewsSummary;
}
