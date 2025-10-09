package com.certimetergroup.smart.travel.exception;

import io.quarkiverse.resteasy.problem.HttpProblem;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;


@Provider
@Slf4j
public class GlobalExceptionHandler {

  @Context
  UriInfo uriInfo;

  @ServerExceptionMapper(FailureException.class)
  public Response handleFailureException(FailureException ex) {
    String title = ex.getMessage().isBlank() ? ex.getResponseEnum().toString()
        : ex.getResponseEnum().getDescription();
    String detail =
        ex.getMessage().isBlank() ? ex.getResponseEnum().getDescription() : ex.getMessage();

    HttpProblem problemDetail = HttpProblem.builder()
        .withStatus(ex.getHttpStatus())
        .withTitle(title)
        .withDetail(detail)
        .withInstance(uriInfo.getRequestUri())
        .build();

    return Response.status(problemDetail.getStatusCode())
        .entity(problemDetail)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  @ServerExceptionMapper(ValidationException.class)
  public Response handleValidationException(ValidationException ex) {
    String title = ex.getResponseEnum().getDescription();
    String detail = ex.getMessage();
    List<String> errors = ex.getViolations()
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

    HttpProblem problemDetail = HttpProblem.builder()
        .withStatus(ex.getHttpStatus())
        .withTitle(title)
        .withDetail(detail)
        .withInstance(uriInfo.getRequestUri())
        .with("errors", errors)
        .build();

    return Response.status(problemDetail.getStatusCode())
        .entity(problemDetail)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  @ServerExceptionMapper(IllegalArgumentException.class)
  public Response handleIllegalArgumentException(IllegalArgumentException ex) {
    HttpProblem problemDetail = HttpProblem.builder()
        .withStatus(Response.Status.BAD_REQUEST.getStatusCode())
        .withTitle(ex.getClass().getSimpleName())
        .withDetail(ex.getMessage())
        .withInstance(uriInfo.getRequestUri())
        .build();

    return Response.status(problemDetail.getStatusCode())
        .entity(problemDetail)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  // Generic exceptions
  @ServerExceptionMapper(Throwable.class)
  public Response handleGenericException(Throwable ex) {
    log.error("Unhandled exception caught", ex);
    HttpProblem problemDetail = HttpProblem.builder()
        .withStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .withTitle("Internal Server Error")
        .withDetail("An unexpected error occurred. Please try again later")
        .withInstance(uriInfo.getRequestUri())
        .build();

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(problemDetail)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}

