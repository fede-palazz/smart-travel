package com.certimetergroup.smart.travel.filter;

import jakarta.ws.rs.QueryParam;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@Schema(name = "FlightFilter", description = "Filtering options for searching flights")
public class FlightFilter {

  @Schema(description = "Flight code")
  @QueryParam("code")
  private String code;

  @Schema(description = "Flight airline")
  @QueryParam("airline")
  private String airline;

  @Schema(description = "Departure city name")
  @QueryParam("fromCity")
  private String fromCity;

  @Schema(description = "Departure region name")
  @QueryParam("fromRegion")
  private String fromRegion;

  @Schema(description = "Departure country name")
  @QueryParam("fromCountry")
  private String fromCountry;

  @Schema(description = "Destination city name")
  @QueryParam("toCity")
  private String toCity;

  @Schema(description = "Destination region name")
  @QueryParam("toRegion")
  private String toRegion;

  @Schema(description = "Destination country name")
  @QueryParam("toCountry")
  private String toCountry;

  @Schema(description = "Flight minimum price")
  @QueryParam("minPrice")
  private Double minPrice;

  @Schema(description = "Flight maximum price")
  @QueryParam("maxPrice")
  private Double maxPrice;

  @Schema(description = "Flight departure date")
  @QueryParam("departureDate")
  private ZonedDateTime departureDate;
}

