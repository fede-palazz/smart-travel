package com.certimetergroup.smart_travel.bff_api.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
public class GraphqlFailureException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final HttpStatus httpStatus;
  private final ProblemDetail problemDetail;

  public GraphqlFailureException(ProblemDetail problemDetail) {
    super(problemDetail.getDetail());
    this.httpStatus = HttpStatus.resolve(problemDetail.getStatus());
    this.problemDetail = problemDetail;
  }

  public GraphqlFailureException(FailureException ex) {
    super(ex.getMessage());
    this.httpStatus = ex.getHttpStatus();
    this.problemDetail = ProblemDetail.forStatusAndDetail(
        ex.getHttpStatus(),
        ex.getMessage()
    );
  }

  @Override
  public String toString() {
    return "FailureException {httpStatus=" + httpStatus + ", problemDetail=" + problemDetail + "}";
  }

}
