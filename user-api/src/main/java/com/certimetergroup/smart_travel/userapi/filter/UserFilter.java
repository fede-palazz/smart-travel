package com.certimetergroup.smart_travel.userapi.filter;

import com.certimetergroup.smart_travel.userapi.enumeration.UserRoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilter {

  private String id;
  private String name; // Either firstname or surname or both
  private String email;
  private UserRoleEnum role;
}
