package com.certimetergroup.smart_travel.bff_api.auth;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shared.UserRoleEnum;

@Getter
@ToString
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final String id;
  private final String email;
  private final List<SimpleGrantedAuthority> authorities;


  @Override
  public String getPassword() {
    return ""; // JWT doesn't require this
  }

  @Override
  public String getUsername() {
    return email; // used as username
  }

  public boolean hasRole(UserRoleEnum role) {
    if (role == null) {
      return false;
    }
    return getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(auth -> auth.equalsIgnoreCase("ROLE_" + role.name()));
  }

  public String getRole() {
    return authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .filter(auth -> auth.startsWith("ROLE_"))
        .map(auth -> auth.substring(5)) // remove "ROLE_" prefix
        .findFirst()
        .orElse(null);
  }
}