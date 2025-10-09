package com.certimetergroup.smart_travel.bff_api.filter;

import java.time.ZonedDateTime;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccommodationFilter {

  private String name;
  private Set<String> types;
  private Set<String> services;
  private String city;
  private String region;
  private String country;
  private String address;
  private Double minDistanceToCenterKm;
  private Double maxDistanceToCenterKm;
  private Double minPricePerNight;
  private Double maxPricePerNight;
  private Double minRating;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private Integer guests;
}

