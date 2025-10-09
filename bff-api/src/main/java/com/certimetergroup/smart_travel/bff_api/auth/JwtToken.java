package com.certimetergroup.smart_travel.bff_api.auth;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class JwtToken extends AbstractAuthenticationToken {

  private final String token;
  private final Object principal;

  // Constructor for unauthenticated token
  public JwtToken(String token) {
    super(null);
    this.token = token;
    this.principal = null;
    setAuthenticated(false);
  }

  public JwtToken(String token, CustomUserDetails principal) {
    super(principal.getAuthorities());
    this.token = token;
    this.principal = principal;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof JwtToken test)) {
      return false;
    }
    if (this.getToken() == null && test.getToken() != null) {
      return false;
    }
    if (this.getToken() != null && !this.getToken().equals(test.getToken())) {
      return false;
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    int code = super.hashCode();
    if (this.getToken() != null) {
      code ^= this.getToken().hashCode();
    }
    return code;
  }
}
