package com.certimetergroup.smart_travel.bff_api.controller;


import com.certimetergroup.smart_travel.bff_api.auth.CustomUserDetails;
import com.certimetergroup.smart_travel.bff_api.dto.auth.request.LoginReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.auth.request.RefreshTokensReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.auth.response.LoginResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.auth.response.RefreshTokensResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.service.AuthService;
import com.certimetergroup.smart_travel.bff_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @MutationMapping("login")
  public Mono<LoginResDTO> login(@Argument LoginReqDTO loginReq) {
    return authService.login(
        loginReq.getEmail(),
        loginReq.getPassword()
    );
  }

  @QueryMapping("profile")
  @PreAuthorize("isAuthenticated()")
  public Mono<UserNoPwdResDTO> profile(@AuthenticationPrincipal CustomUserDetails user) {
    return userService.getUserById(user.getId());
  }

  @MutationMapping("refresh")
  public Mono<RefreshTokensResDTO> refresh(@Argument RefreshTokensReqDTO refreshTokenReq) {
    return authService.areTokensValid(refreshTokenReq.getAccessToken(),
            refreshTokenReq.getRefreshToken())
        .flatMap(valid -> {
          if (!valid) {
            return Mono.error(new GraphqlFailureException(
                ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                    "Invalid or expired tokens")));
          }
          // Extract user info from the refresh token
          return authService.getUser(refreshTokenReq.getRefreshToken())
              .flatMap(userDetails -> {
                // Generate new tokens
                String newAccessToken = authService.generateAccessToken(userDetails);
                String newRefreshToken = authService.generateRefreshToken(userDetails);

                log.info("Refreshed access token: {}", newAccessToken);

                return Mono.just(new RefreshTokensResDTO(
                    newAccessToken,
                    newRefreshToken
                ));
              });
        });
  }

}
