package com.certimetergroup.smart.travel.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.Coordinates;
import shared.DestinationSummary;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationReqDTO {

  @NotBlank(message = "Parameter 'name' is required")
  private String name;

  @NotBlank(message = "Parameter 'type' is required")
  private String type;

  private Set<String> services = new HashSet<>();

  @NotNull(message = "Parameter 'destination' is required")
  @Valid
  private DestinationSummary destination;

  @NotBlank(message = "Parameter 'address' is required")
  private String address;

  @NotNull(message = "Parameter 'coordinates' is required")
  @Valid
  private Coordinates coordinates;

  @NotBlank(message = "Parameter 'mainPicture' is required")
  private String mainPicture;

}
