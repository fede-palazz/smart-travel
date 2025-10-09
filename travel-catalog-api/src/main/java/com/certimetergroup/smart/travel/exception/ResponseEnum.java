package com.certimetergroup.smart.travel.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;

@Getter
@AllArgsConstructor
public enum ResponseEnum {
  SUCCESS(200, "Success", StatusCode.OK),
  FORBIDDEN(403, "Missing permissions", StatusCode.FORBIDDEN),
  NOT_FOUND(404, "Resource not found", StatusCode.NOT_FOUND),
  INVALID_PARAM(4000, "Invalid request param", StatusCode.BAD_REQUEST),
  INVALID_BODY(4001, "Invalid request body", StatusCode.BAD_REQUEST),
  VALIDATION_FAILED(4002, "Input validation failed", StatusCode.BAD_REQUEST),
  UNEXPECTED_ERROR(500, "Generic error", StatusCode.INTERNAL_SERVER_ERROR),

  // AgencyPackage errors
  PACKAGE_PUBLISHED(1001, "Package already published", StatusCode.CONFLICT),
  PACKAGE_ARCHIVED(1002, "Package set as archived", StatusCode.CONFLICT),
  PACKAGE_DRAFT(1003, "Package set as draft", StatusCode.CONFLICT);

  private final int id;
  private final String description;
  private final int httpStatus;

  @Override
  public String toString() {
    // Replace underscores with spaces
    String result = name().replace('_', ' ').toLowerCase();
    // Capitalize the first character and leave the rest lowercase
    return result.substring(0, 1).toUpperCase() + result.substring(1);
  }
}

