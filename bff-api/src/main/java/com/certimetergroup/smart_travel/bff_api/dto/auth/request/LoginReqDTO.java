package com.certimetergroup.smart_travel.bff_api.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginReqDTO {

  @NotBlank(message = "Parameter 'email' is required")
  @Email(message = "Parameter 'email' must represent a valid email address")
  @Size(max = 100, message = "Parameter 'email' must have a maximum length of 100 characters")
  private String email;

  @NotBlank(message = "Parameter 'password' is required")
  @Size(min = 5, max = 25, message = "Parameter 'password' must have a length between 5 and 25 characters")
  private String password;
}
