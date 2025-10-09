package com.certimetergroup.smart_travel.bff_api.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GraphqlExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    Map<String, Object> extensions = new HashMap<>();

    switch (ex) {
      case GraphqlFailureException e -> {
        ProblemDetail problemDetail = e.getProblemDetail();

        // Add addition error info
        extensions.put("title", problemDetail.getTitle());
        extensions.put("details", problemDetail.getDetail());
        extensions.put("status", e.getHttpStatus());
        extensions.put("statusCode", problemDetail.getStatus());

        // Check for input violations
        if (problemDetail.getProperties() != null) {
          Object errorsObj = problemDetail.getProperties().get("errors");
          if (errorsObj instanceof List<?> rawList &&
              rawList.stream().allMatch(item -> item instanceof String)) {
            List<String> errors = rawList.stream()
                .map(item -> (String) item)
                .toList();
            extensions.put("errors", errors);
          }
        }

        ErrorType errorType = switch (problemDetail.getStatus()) {
          case 400 -> ErrorType.BAD_REQUEST;
          case 401 -> ErrorType.UNAUTHORIZED;
          case 403 -> ErrorType.FORBIDDEN;
          case 404 -> ErrorType.NOT_FOUND;
          default -> ErrorType.INTERNAL_ERROR;
        };

        return GraphqlErrorBuilder.newError()
            .errorType(errorType)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath())
            .location(env.getField().getSourceLocation())
            .extensions(extensions)
            .build();
      }
      case AuthenticationCredentialsNotFoundException e -> {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
            "Authentication is required to access this resource");
        problemDetail.setTitle("Unauthorized access");

        // Add addition error info
        extensions.put("title", problemDetail.getTitle());
        extensions.put("details", problemDetail.getDetail());
        extensions.put("status", status.name());
        extensions.put("statusCode", status.value());

        ErrorType errorType = ErrorType.UNAUTHORIZED;

        return GraphqlErrorBuilder.newError()
            .errorType(errorType)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath())
            .location(env.getField().getSourceLocation())
            .extensions(extensions)
            .build();
      }
      case AuthorizationDeniedException e -> {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
            "You don't have the permission to access this resource");
        problemDetail.setTitle("Forbidden access");

        // Add addition error info
        extensions.put("title", problemDetail.getTitle());
        extensions.put("details", problemDetail.getDetail());
        extensions.put("status", status.name());
        extensions.put("statusCode", status.value());

        ErrorType errorType = ErrorType.UNAUTHORIZED;

        return GraphqlErrorBuilder.newError()
            .errorType(errorType)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath())
            .location(env.getField().getSourceLocation())
            .extensions(extensions)
            .build();
      }
      default -> {
      }
    }
    // Default handler
    log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

    // Add addition error info
    extensions.put("title", "Internal server error");
    extensions.put("details", "An unexpected error occurred, please try again");
    extensions.put("status", HttpStatus.INTERNAL_SERVER_ERROR.name());
    extensions.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());

    return GraphqlErrorBuilder.newError()
        .errorType(ErrorType.INTERNAL_ERROR)
        .message((String) extensions.get("details"))
        .path(env.getExecutionStepInfo().getPath())
        .location(env.getField().getSourceLocation())
        .extensions(extensions)
        .build();
  }
}
