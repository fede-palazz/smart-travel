package com.certimetergroup.smart_travel.ordersapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ResponseEnum {
  SUCCESS(200, "success", HttpStatus.OK),
  FORBIDDEN(403, "missing permissions", HttpStatus.FORBIDDEN),
  NOT_FOUND(404, "resource not found", HttpStatus.NOT_FOUND),
  INVALID_INPUT(422, "input validation failed", HttpStatus.UNPROCESSABLE_ENTITY),
  UNEXPECTED_ERROR(500, "generic error", HttpStatus.INTERNAL_SERVER_ERROR),

  ORDER_NOT_FOUND(1404, "Order not found", HttpStatus.NOT_FOUND),
  ORDER_PENDING(1405, "Pending order", HttpStatus.CONFLICT),
  ;

  private final int id;
  private final String description;
  private final HttpStatus httpStatus;

  @Override
  public String toString() {
    // Replace underscores with spaces
    String result = name().replace('_', ' ').toLowerCase();
    // Capitalize the first character and leave the rest lowercase
    return result.substring(0, 1).toUpperCase() + result.substring(1);
  }
}
