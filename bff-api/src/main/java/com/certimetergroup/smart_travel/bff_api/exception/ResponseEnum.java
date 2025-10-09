package com.certimetergroup.smart_travel.bff_api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ResponseEnum {
  SUCCESS(200, "Success", HttpStatus.OK),
  FORBIDDEN(403, "Missing permissions", HttpStatus.FORBIDDEN),
  NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
  INVALID_INPUT(422, "Input validation failed", HttpStatus.UNPROCESSABLE_ENTITY),
  UNEXPECTED_ERROR(500, "Generic error", HttpStatus.INTERNAL_SERVER_ERROR),

  // Authentication errors
  EXPIRED_ACCESS_TOKEN(1420, "Expired access token", HttpStatus.UNAUTHORIZED),
  INVALID_ACCESS_TOKEN(1421, "Invalid access token", HttpStatus.UNAUTHORIZED),
  MALFORMED_ACCESS_TOKEN(1422, "Malformed access token", HttpStatus.BAD_REQUEST),
  INVALID_REFRESH_TOKEN(1430, "Invalid refresh token", HttpStatus.UNAUTHORIZED),
  MALFORMED_REFRESH_TOKEN(1431, "Malformed refresh token", HttpStatus.BAD_REQUEST),
  EXPIRED_REFRESH_TOKEN(1432, "Expired refresh token", HttpStatus.BAD_REQUEST),
  TOKEN_MISMATCH_ERROR(1440, "Token mismatch", HttpStatus.BAD_REQUEST),
  INVALID_CREDENTIALS(1441, "Invalid credentials", HttpStatus.UNAUTHORIZED),

  USER_NOT_FOUND(4004, "User not found", HttpStatus.NOT_FOUND),
  EMAIL_ALREADY_EXISTS(4005, "Email already exists", HttpStatus.CONFLICT),
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
