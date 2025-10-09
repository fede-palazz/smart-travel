package com.certimetergroup.smart_travel.userapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReqDTO {

  @NotBlank(message = "Parameter 'email' is required")
  @Email(message = "Parameter 'email' must represent a valid email")
  private String email;

  @NotBlank(message = "Parameter 'password' is required")
  @Size(min = 5, max = 25, message = "Parameter 'password' must have a length between 5 and 25 characters")
  private String password;

  @NotBlank(message = "Parameter 'role' is required")
  private String role;

  @NotBlank(message = "Parameter 'firstname' is required")
  private String firstname;

  @NotBlank(message = "Parameter 'lastname' is required")
  private String lastname;
}
