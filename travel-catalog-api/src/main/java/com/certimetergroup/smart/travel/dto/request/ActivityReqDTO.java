package com.certimetergroup.smart.travel.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.Coordinates;
import shared.DestinationSummary;
import shared.Price;
import shared.Schedule;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReqDTO {

  @NotBlank(message = "Parameter 'name' is required")
  private String name;

  @NotBlank(message = "Parameter 'type' is required")
  private String type;

  @NotBlank(message = "Parameter 'description' is required")
  private String description;

  private String notes;

  @NotBlank(message = "Parameter 'address' is required")
  private String address;

  @NotNull(message = "Parameter 'coordinates' is required")
  @Valid
  private Coordinates coordinates;

  @NotNull(message = "Parameter 'destination' is required")
  @Valid
  private DestinationSummary destination;

  @NotBlank(message = "Parameter 'mainPicture' is required")
  private String mainPicture;

  private Set<String> pictures = new HashSet<>();

  private Set<String> tags = new HashSet<>();

  private Set<String> languages = new HashSet<>();

  @NotNull(message = "Parameter 'schedule' is required")
  @Valid
  private Schedule schedule;

  @NotNull(message = "Parameter 'price' is required")
  @Valid
  private Price price;

  /**
   * Leave these to maintain the corresponding imports
   */
  @PositiveOrZero
  private Double toImportAnnotationPositive;
}
