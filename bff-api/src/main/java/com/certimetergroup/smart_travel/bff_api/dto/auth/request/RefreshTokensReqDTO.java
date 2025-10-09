package com.certimetergroup.smart_travel.bff_api.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokensReqDTO {

  @NotBlank(message = "Parameter 'refreshToken' is required")
  private final String refreshToken;

  @NotBlank(message = "Parameter 'accessToken' is required")
  private final String accessToken;
}
