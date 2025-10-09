package com.certimetergroup.smart.travel.dto.request;


import com.certimetergroup.smart.travel.validators.TimeZone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.Coordinates;
import shared.Country;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationReqDTO {

  @NotBlank(message = "Parameter 'city' is required")
  private String city;

  @NotBlank(message = "Parameter 'region' is required")
  private String region;

  @NotNull(message = "Parameter 'country' is required")
  @Valid
  private Country country;

  @NotNull(message = "Parameter 'coordinates' is required")
  @Valid
  private Coordinates coordinates;

  @NotBlank(message = "Parameter 'description' is required")
  private String description;

  private Set<String> pictures = new HashSet<>();

  private Set<String> tags = new HashSet<>();

  @NotNull(message = "Parameter 'popularityScore' is required")
  @Min(value = 0, message = "Parameter 'popularityScore' must have a value included between 0 and 100")
  @Max(value = 100, message = "Parameter 'popularityScore' must have a value included between 0 and 100")
  private Integer popularityScore;

  @NotBlank(message = "Parameter 'timezone' is required")
  @TimeZone(message = "Parameter 'timezone' must represent a valid timezone")
  private String timezone;
}
