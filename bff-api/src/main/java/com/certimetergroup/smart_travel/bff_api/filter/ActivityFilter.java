package com.certimetergroup.smart_travel.bff_api.filter;

import java.time.ZonedDateTime;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivityFilter {

  private String name;
  private Set<String> types;
  private String description;
  private String address;
  private String city;
  private String region;
  private String country;
  private Set<String> tags;
  private Set<String> languages;
  private Double minPrice;
  private Double maxPrice;
  private Double minRating;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
}

