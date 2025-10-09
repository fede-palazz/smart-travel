package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CancelOrderReqDTO {

  @NotBlank(message = "Parameter 'token' is required")
  String token;
}
