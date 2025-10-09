package com.certimetergroup.smart_travel.userapi.exception;

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
  USERNAME_ALREADY_EXISTS(1400, "username already exists", HttpStatus.CONFLICT),
  EXPIRED_ACCESS_TOKEN(1420, "expired access token", HttpStatus.UNAUTHORIZED),
  INVALID_ACCESS_TOKEN(1421, "invalid access token", HttpStatus.UNAUTHORIZED),
  MALFORMED_ACCESS_TOKEN(1422, "malformed access token", HttpStatus.BAD_REQUEST),
  INVALID_REFRESH_TOKEN(1430, "invalid refresh token", HttpStatus.UNAUTHORIZED),
  MALFORMED_REFRESH_TOKEN(1431, "malformed refresh token", HttpStatus.BAD_REQUEST),
  TOKEN_MISMATCH_ERROR(1432, "token mismatch", HttpStatus.BAD_REQUEST),
  INVALID_CREDENTIALS(1440, "invalid credentials", HttpStatus.UNAUTHORIZED),

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
