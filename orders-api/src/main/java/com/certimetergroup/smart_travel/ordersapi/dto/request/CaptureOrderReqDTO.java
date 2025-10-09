package com.certimetergroup.smart_travel.ordersapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptureOrderReqDTO {

  @NotBlank(message = "Parameter 'token' is required")
  String token;

  @NotBlank(message = "Parameter 'payerId' is required")
  String payerId;
}
