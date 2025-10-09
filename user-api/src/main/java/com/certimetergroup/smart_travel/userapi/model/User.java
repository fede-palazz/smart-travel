package com.certimetergroup.smart_travel.userapi.model;

import com.certimetergroup.smart_travel.userapi.enumeration.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

  @Id
  private String id;
  private String email;
  private String password;
  private UserRoleEnum role;
  private String firstname;
  private String lastname;
}
