package com.certimetergroup.smart_travel.userapi.validator;


import com.certimetergroup.smart_travel.userapi.exception.FailureException;
import com.certimetergroup.smart_travel.userapi.exception.ResponseEnum;
import java.util.List;

public class ControllerValidator {

  public static void validate(int size, int page, String sort, String order,
      List<String> allowedSortValues) {
    if (size <= 0 || size > 50) {
      throw new FailureException(ResponseEnum.INVALID_INPUT,
          "Param 'size' must be a number between 1 and 50");
    }
    if (page < 0) {
      throw new FailureException(ResponseEnum.INVALID_INPUT,
          "Param 'page' must be greater or equal than 0");
    }
    if (!order.equals("asc") && !order.equals("desc")) {
      throw new FailureException(ResponseEnum.INVALID_INPUT,
          "Param 'order' should be either 'asc' or 'desc'");
    }
    if (!allowedSortValues.contains(sort)) {
      throw new FailureException(ResponseEnum.INVALID_INPUT,
          "Param 'sort' should be equal to one of the following values: " + allowedSortValues);
    }
  }
}
