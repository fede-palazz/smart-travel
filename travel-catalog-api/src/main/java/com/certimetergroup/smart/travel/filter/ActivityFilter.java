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
@Schema(name = "ActivityFilter", description = "Filtering options for searching activities")
public class ActivityFilter {

  @Schema(description = "Activity name")
  @QueryParam("name")
  private String name;

  @Schema(description = "Activity types")
  @QueryParam("types")
  private Set<String> types;

  @Schema(description = "Activity description")
  @QueryParam("description")
  private String description;

  @Schema(description = "Activity address")
  @QueryParam("address")
  private String address;

  @Schema(description = "Activity city name")
  @QueryParam("city")
  private String city;

  @Schema(description = "Activity region name")
  @QueryParam("region")
  private String region;

  @Schema(description = "Activity country name")
  @QueryParam("country")
  private String country;

  @Schema(description = "Activity tags")
  @QueryParam("tags")
  private Set<String> tags;

  @Schema(description = "Activity languages")
  @QueryParam("languages")
  private Set<String> languages;

  @Schema(description = "Activity minimum price")
  @QueryParam("minPrice")
  private Double minPrice;

  @Schema(description = "Activity maximum price")
  @QueryParam("maxPrice")
  private Double maxPrice;

  @Schema(description = "Activity minimum rating")
  @QueryParam("minRating")
  private Double minRating;

  @Schema(description = "Activity minimum start date")
  @QueryParam("startDate")
  private ZonedDateTime startDate;

  @Schema(description = "Activity end date")
  @QueryParam("endDate")
  private ZonedDateTime endDate;
}

