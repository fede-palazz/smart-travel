package com.certimetergroup.smart_travel.bff_api.auth;

import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import com.certimetergroup.smart_travel.bff_api.service.JwtService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
class JwtAuthenticationManager implements ReactiveAuthenticationManager {

  private final JwtService jwtService;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    return Mono.justOrEmpty(authentication)
        .filter(auth -> auth instanceof JwtToken)
        .cast(JwtToken.class)
        .flatMap(jwtToken -> {
          try {
            String token = jwtToken.getToken();
            // Validate access token
            jwtService.validateAccessToken(token);
            CustomUserDetails userDetails = createUserDetails(token);

            return Mono.just((Authentication) new JwtToken(token, userDetails));
          } catch (FailureException ex) {
            if (ex.getResponseEnum().equals(ResponseEnum.EXPIRED_ACCESS_TOKEN)) {
              return Mono.error(new CredentialsExpiredException("Expired access token"));
            }
            return Mono.error(new BadCredentialsException("Invalid access token"));
          }
        })
        .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid or missing token")));
  }

  private CustomUserDetails createUserDetails(String token) {
    String userId = jwtService.getClaimFromAccessToken(token, JwtClaim.ID, String.class);
    String email = jwtService.getClaimFromAccessToken(token, JwtClaim.EMAIL, String.class);
    String role = jwtService.getClaimFromAccessToken(token, JwtClaim.ROLE, String.class);
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

    return new CustomUserDetails(userId, email, authorities);
  }
}
