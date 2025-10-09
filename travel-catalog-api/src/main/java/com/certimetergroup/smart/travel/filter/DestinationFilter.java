package com.certimetergroup.smart.travel.filter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.QueryParam;
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
@Schema(name = "DestinationFilter", description = "Filtering options for searching destinations")
public class DestinationFilter {

  @QueryParam("city")
  @Schema(description = "City name")
  private String city;

  @QueryParam("region")
  @Schema(description = "Region name")
  private String region;

  @QueryParam("countryCode")
  @Schema(description = "Country ISO code (e.g. IT, US)")
  private String countryCode;

  @QueryParam("minPopularity")
  @Min(value = 0, message = "Parameter 'minPopularity' must have a value included between 0 and 100")
  @Max(value = 100, message = "Parameter 'minPopularity' must have a value included between 0 and 100")
  @Schema(description = "Minimum popularity score")
  private Integer minPopularity;

  @QueryParam("maxPopularity")
  @Min(value = 0, message = "Parameter 'maxPopularity' must have a value included between 0 and 100")
  @Max(value = 100, message = "Parameter 'maxPopularity' must have a value included between 0 and 100")
  @Schema(description = "Maximum popularity score")
  private Integer maxPopularity;

  @QueryParam("tags")
  @Schema(description = "Tags to match")
  private Set<String> tags;
}

