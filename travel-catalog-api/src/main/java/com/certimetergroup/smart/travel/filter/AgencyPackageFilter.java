package com.certimetergroup.smart.travel.filter;

import jakarta.ws.rs.QueryParam;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import shared.PackageStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@Schema(name = "AgencyPackageFilter", description = "Filtering options for searching agency packages")
public class AgencyPackageFilter {

  @Schema(description = "Agency package name")
  @QueryParam("name")
  private String name;

  @Schema(description = "Agency package city name")
  @QueryParam("city")
  private String city;

  @Schema(description = "Agency package region name")
  @QueryParam("region")
  private String region;

  @Schema(description = "Agency package country name")
  @QueryParam("country")
  private String country;

  @Schema(description = "Agency package tags")
  @QueryParam("tags")
  private Set<String> tags;

  @Schema(description = "Agency package status")
  @QueryParam("status")
  private PackageStatus status;

  @Schema(description = "Agency package minimum price")
  @QueryParam("minPrice")
  private Double minPrice;

  @Schema(description = "Agency package maximum price")
  @QueryParam("maxPrice")
  private Double maxPrice;

  @Schema(description = "Agency package minimum start date")
  @QueryParam("startDate")
  private Instant startDate;

  @Schema(description = "Agency package maximum end date")
  @QueryParam("endDate")
  private Instant endDate;

  @Schema(description = "Agency package author's id (agent who created it)")
  @QueryParam("authorId")
  private String authorId;
}

