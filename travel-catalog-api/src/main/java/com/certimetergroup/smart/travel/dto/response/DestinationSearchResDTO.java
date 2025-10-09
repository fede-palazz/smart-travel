package com.certimetergroup.smart.travel.dto.response;


import com.certimetergroup.smart.travel.enums.DestinationType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DestinationSearchResDTO {

  private String name;
  private DestinationType type;
}
