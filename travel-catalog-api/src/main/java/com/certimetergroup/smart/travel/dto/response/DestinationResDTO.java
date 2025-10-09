package com.certimetergroup.smart.travel.dto.response;


import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import shared.Coordinates;
import shared.Country;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationResDTO {

  private ObjectId id;
  private String city;
  private String region;
  private Country country;
  private Coordinates coordinates;
  private String description;
  private Set<String> pictures;
  private Set<String> tags;
  private Integer popularityScore;
  private String timezone;
}
