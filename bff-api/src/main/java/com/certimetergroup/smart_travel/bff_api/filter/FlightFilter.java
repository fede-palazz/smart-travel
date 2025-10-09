package com.certimetergroup.smart_travel.bff_api.filter;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlightFilter {

  private String code;
  private String airline;
  private String fromCity;
  private String fromRegion;
  private String fromCountry;
  private String toCity;
  private String toRegion;
  private String toCountry;
  private Double minPrice;
  private Double maxPrice;
  private ZonedDateTime departureDate;
}

