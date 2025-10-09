package com.certimetergroup.smart_travel.userapi.enumeration;

import com.certimetergroup.smart_travel.userapi.exception.FailureException;
import com.certimetergroup.smart_travel.userapi.exception.ResponseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserRoleEnum {
  ADMIN,
  AGENT,
  CUSTOMER;

  @JsonCreator
  public static UserRoleEnum from(String value) {
    for (UserRoleEnum role : UserRoleEnum.values()) {
      if (role.name().equalsIgnoreCase(value)) {
        return role;
      }
    }
    throw new FailureException(ResponseEnum.INVALID_INPUT, "Invalid user role: " + value);
  }
}
