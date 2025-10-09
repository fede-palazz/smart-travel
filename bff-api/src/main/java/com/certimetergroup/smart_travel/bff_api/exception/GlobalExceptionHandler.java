package com.certimetergroup.smart_travel.bff_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalExceptionHandler implements WebExceptionHandler {

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    ServerHttpResponse response = exchange.getResponse();
    response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    // Default to 500
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ProblemDetail problemDetail;

    // Failure exception handler
    if (ex instanceof FailureException failureEx) {
      String title = failureEx.getMessage().isBlank() ? failureEx.getResponseEnum().toString()
          : failureEx.getResponseEnum().getDescription();
      String detail =
          failureEx.getMessage().isBlank() ? failureEx.getResponseEnum().getDescription()
              : failureEx.getMessage();
      status = failureEx.getHttpStatus();

      problemDetail = ProblemDetail.forStatusAndDetail(
          status,
          detail
      );
      problemDetail.setTitle(title);
    }
    // IllegalArgumentException handler
    else if (ex instanceof IllegalArgumentException illegalArgumentEx) {
      problemDetail = ProblemDetail.forStatusAndDetail(
          HttpStatus.BAD_REQUEST,
          illegalArgumentEx.getMessage()
      );
      problemDetail.setTitle(illegalArgumentEx.getClass().getSimpleName());
    }
    // Default Exception handler
    else {
      problemDetail = ProblemDetail.forStatusAndDetail(status, "Unexpected error occurred.");
      problemDetail.setTitle("Internal Server Error");
    }
    // Set response status code
    response.setStatusCode(status);

    // Write response as JSON
    return response.writeWith(
        Mono.fromSupplier(() -> {
          try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(problemDetail);
            return response.bufferFactory().wrap(bytes);
          } catch (Exception e) {
            log.error("Failed to write error response", e);
            return response.bufferFactory().wrap(new byte[0]);
          }
        })
    );
  }
}
