package com.certimetergroup.smart_travel.userapi.dto;

import com.certimetergroup.smart_travel.userapi.enumeration.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResDTO {

  private String id;
  private String email;
  private String password;
  private UserRoleEnum role;
  private String firstname;
  private String lastname;
  private String fullname;
}
