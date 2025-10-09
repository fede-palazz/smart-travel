package com.certimetergroup.smart_travel.bff_api.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtClaim {
  ID("id"),
  EMAIL("email"),
  ROLE("role"),
  ISSUED_AT("iat");

  private final String description;
}
