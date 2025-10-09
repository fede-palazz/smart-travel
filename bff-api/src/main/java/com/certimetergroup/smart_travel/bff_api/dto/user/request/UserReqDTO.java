package com.certimetergroup.smart_travel.bff_api.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReqDTO {

  private String email;
  private String password;
  private String role;
  private String firstname;
  private String lastname;
}
