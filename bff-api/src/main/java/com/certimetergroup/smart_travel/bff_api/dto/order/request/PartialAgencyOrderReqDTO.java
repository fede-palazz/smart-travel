package com.certimetergroup.smart_travel.bff_api.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PartialAgencyOrderReqDTO {

  @NotBlank(message = "Parameter 'agencyPackageId' is required")
  @Size(min = 24, max = 24, message = "Parameter 'agencyPackageId' is invalid")
  private String agencyPackageId; // In case of agency package
}
