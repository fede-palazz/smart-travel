package com.certimetergroup.smart.travel.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DestinationType {
  CITY("city"),
  REGION("region"),
  COUNTRY("country");

  private final String value;

  DestinationType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
