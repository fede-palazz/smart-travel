package com.certimetergroup.smart_travel.ordersapi.exception;

import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public class FailureException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final HttpStatus httpStatus;
  private final ResponseEnum responseEnum;

  public FailureException(ResponseEnum responseEnum, String message, Throwable exception) {
    super(message, exception);
    this.httpStatus = responseEnum.getHttpStatus();
    this.responseEnum = responseEnum;
  }

  public FailureException(ResponseEnum responseEnum, String message) {
    super(message);
    this.httpStatus = responseEnum.getHttpStatus();
    this.responseEnum = responseEnum;
  }

  @Override
  public String toString() {
    return "FailureException {httpStatus=" + httpStatus + ", responseErrorEnum=" + responseEnum
        + "}";
  }

}
