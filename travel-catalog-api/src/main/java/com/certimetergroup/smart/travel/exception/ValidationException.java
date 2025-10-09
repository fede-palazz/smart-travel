package com.certimetergroup.smart.travel.exception;

import jakarta.validation.ConstraintViolation;
import java.io.Serial;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 2L;
  private final int httpStatus;
  private final ResponseEnum responseEnum;
  Set<? extends ConstraintViolation<?>> violations;

  public ValidationException(Set<? extends ConstraintViolation<?>> violations) {
    super("One or more input parameters are invalid");
    this.httpStatus = ResponseEnum.VALIDATION_FAILED.getHttpStatus();
    this.responseEnum = ResponseEnum.VALIDATION_FAILED;
    this.violations = violations;
  }

}
