package com.certimetergroup.smart.travel.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.Contacts;
import shared.Coordinates;
import shared.Policies;
import shared.Room;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationUpdateReqDTO {

  // Accommodation info

  @NotBlank(message = "Parameter 'name' is required")
  private String name;

  @NotBlank(message = "Parameter 'type' is required")
  private String type;

  private Set<String> services = new HashSet<>();

  @NotBlank(message = "Parameter 'address' is required")
  private String address;

  @NotNull(message = "Parameter 'coordinates' is required")
  @Valid
  private Coordinates coordinates;

  @NotBlank(message = "Parameter 'mainPicture' is required")
  private String mainPicture;

  // Accommodation details info

  @NotBlank(message = "Parameter 'description' is required")
  private String description;

  private String details;

  @NotBlank(message = "Parameter 'checkInTime' is required")
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Parameter 'checkInTime' must be in HH:mm 24-hour format")
  private String checkInTime;

  @NotBlank(message = "Parameter 'checkOutTime' is required")
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Parameter 'checkOutTime' must be in HH:mm 24-hour format")
  private String checkOutTime;

  private Set<String> pictures;

  @NotNull(message = "Parameter 'contacts' is required")
  @Valid
  private Contacts contacts;

  @NotNull(message = "Parameter 'policies' is required")
  @Valid
  private Policies policies;

  private Set<String> languages = new HashSet<>();

  private Set<Room> rooms = new HashSet<>();
}
