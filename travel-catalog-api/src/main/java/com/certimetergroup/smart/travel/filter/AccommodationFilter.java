package com.certimetergroup.smart.travel.filter;

import jakarta.ws.rs.QueryParam;
import java.time.ZonedDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@Schema(name = "AccommodationFilter", description = "Filtering options for searching accommodations")
public class AccommodationFilter {

  @Schema(description = "Accommodation name")
  @QueryParam("name")
  private String name;

  @Schema(description = "Accommodation types")
  @QueryParam("types")
  private Set<String> types;

  @Schema(description = "Accommodation services")
  @QueryParam("services")
  private Set<String> services;

  @Schema(description = "Accommodation's city name")
  @QueryParam("city")
  private String city;

  @Schema(description = "Accommodation's region name")
  @QueryParam("region")
  private String region;

  @Schema(description = "Accommodation's country name")
  @QueryParam("country")
  private String country;

  @Schema(description = "Accommodation address")
  @QueryParam("address")
  private String address;

  @Schema(description = "Accommodation minimum distance to center in km")
  @QueryParam("minDistanceToCenterKm")
  private Double minDistanceToCenterKm;

  @Schema(description = "Accommodation maximum distance to center in km")
  @QueryParam("maxDistanceToCenterKm")
  private Double maxDistanceToCenterKm;

  @Schema(description = "Accommodation minimum price per night")
  @QueryParam("minPricePerNight")
  private Double minPricePerNight;

  @Schema(description = "Accommodation maximum price per night")
  @QueryParam("maxPricePerNight")
  private Double maxPricePerNight;

  @Schema(description = "Accommodation minimum rating")
  @QueryParam("minRating")
  private Double minRating;

  @Schema(description = "Planned arrival date")
  @QueryParam("startDate")
  private ZonedDateTime startDate;

  @Schema(description = "Planned leaving date")
  @QueryParam("endDate")
  private ZonedDateTime endDate;

  @Schema(description = "Total number of guests")
  @QueryParam("guests")
  private Integer guests;
}

