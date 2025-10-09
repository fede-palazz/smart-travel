package com.certimetergroup.smart_travel.ordersapi.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Request parameter validation failed"
    );

    List<String> errors = ex.getConstraintViolations()
        .stream()
        .map(violation -> {
          String path = violation.getPropertyPath().toString();
          String[] parts = path.split("\\.");
          path = parts.length > 2
              ? String.join(".", Arrays.copyOfRange(parts, 2, parts.length))
              : path;
          String message = violation.getMessage();
          return path + ": " + message;
        })
        .toList();

    problemDetail.setTitle("Validation error");
    problemDetail.setProperty("errors", errors);
    return problemDetail;
  }

  @ExceptionHandler(FailureException.class)
  public ResponseEntity<ProblemDetail> handleFailureException(FailureException ex) {
    HttpStatus status = ex.getHttpStatus();
    String title = ex.getMessage().isBlank()
        ? ex.getResponseEnum().toString()
        : ex.getResponseEnum().getDescription();
    String detail = ex.getMessage().isBlank()
        ? ex.getResponseEnum().getDescription()
        : ex.getMessage();
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setTitle(title);

    return ResponseEntity.status(status).body(problemDetail);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        ex.getMessage()
    );
    problemDetail.setTitle(ex.getClass().getSimpleName());
    return problemDetail;
  }


  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Request validation failed"
    );
    String message =
        ex.getMessage().contains("Cannot deserialize value of type `java.time.LocalDate`") ?
            "Field 'birthday' should represent a valid date" :
            ex.getMessage();

    problemDetail.setTitle("Validation error");
    problemDetail.setDetail(message);
    return problemDetail;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unknown problem occurred, please try again"
    );
    problemDetail.setTitle("Internal server error");
    log.error(ex.getMessage(), ex);
    return problemDetail;
  }
}

