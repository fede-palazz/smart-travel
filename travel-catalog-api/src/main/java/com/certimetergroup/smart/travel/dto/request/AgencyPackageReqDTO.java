package com.certimetergroup.smart.travel.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.DestinationSummary;
import shared.Price;
import shared.UserSummary;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyPackageReqDTO {

  @NotBlank(message = "Parameter 'name' is required")
  private String name;

  @NotBlank(message = "Parameter 'description' is required")
  private String description;

  @NotNull(message = "Parameter 'tags' is required")
  private Set<String> tags;

  @NotNull(message = "Parameter 'startDate' is required")
  private Instant startDate;

  @NotNull(message = "Parameter 'endDate' is required")
  private Instant endDate;

  @NotNull(message = "Parameter 'totalPrice' is required")
  @Valid
  private Price totalPrice;

  @NotNull(message = "Parameter 'destination' is required")
  @Valid
  private DestinationSummary destination;

  @NotBlank(message = "Parameter 'mainPicture' is required")
  private String mainPicture;

  private Set<String> pictures;

  @NotNull(message = "Parameter 'departureFlight' is required")
  @Valid
  private FlightOrder departureFlight;

  @NotNull(message = "Parameter 'returnFlight' is required")
  @Valid
  private FlightOrder returnFlight;

  @NotNull(message = "Parameter 'accommodation' is required")
  @Valid
  private AccommodationOrder accommodation;

  @NotNull(message = "Parameter 'activities' is required")
  @Size(min = 1, message = "Parameter 'activities' must contain at least one element")
  @Valid
  private Set<ActivityOrder> activities;

  @NotNull(message = "Parameter 'agentInfo' is required")
  @Valid
  private UserSummary agentInfo;

  @NotNull(message = "Parameter 'quantity' is required")
  @Positive(message = "Parameter 'quantity' must represent a positive integer")
  private Integer quantity;
}
