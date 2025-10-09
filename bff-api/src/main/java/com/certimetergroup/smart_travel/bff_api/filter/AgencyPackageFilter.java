package com.certimetergroup.smart_travel.bff_api.filter;

import java.time.Instant;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.PackageStatus;

@Data
@NoArgsConstructor
public class AgencyPackageFilter {

  private String name;
  private String city;
  private String region;
  private String country;
  private Set<String> tags;
  private PackageStatus status;
  private Double minPrice;
  private Double maxPrice;
  private Instant startDate;
  private Instant endDate;
  private String authorId;
}

