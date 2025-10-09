package com.certimetergroup.smart_travel.bff_api.filter;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DestinationFilter {

  private String city;
  private String region;
  private String countryCode;
  private Integer minPopularity;
  private Integer maxPopularity;
  private Set<String> tags;
}

