package com.certimetergroup.smart_travel.bff_api.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import shared.UserRoleEnum;

@Data
@AllArgsConstructor
public class UserNoPwdResDTO {

  private String id;
  private String email;
  private UserRoleEnum role;
  private String firstname;
  private String lastname;
  private String fullname;
}
