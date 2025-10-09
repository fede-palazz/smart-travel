package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptureOrderReqDTO {

  String token;
  String payerId;
}
