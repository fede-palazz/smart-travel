package com.certimetergroup.smart_travel.bff_api.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import shared.UserRoleEnum;

@Data
@NoArgsConstructor
public class UserFilter {

  private String id;
  private String name; // Either firstname or surname or both
  private String email;
  private UserRoleEnum role;
}
