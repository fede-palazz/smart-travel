package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.auth.JwtClaim;
import com.certimetergroup.smart_travel.bff_api.dto.auth.response.LoginResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import com.certimetergroup.smart_travel.bff_api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import shared.User;
import shared.UserRoleEnum;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserService userService;
  private final JwtService jwtService;
  private final UserMapper userMapper;


  @Override
  public Mono<LoginResDTO> login(String email, String password) {
    // Get user from db
    return userService.getActualUserByEmail(email)
        .switchIfEmpty(Mono.error(
            new FailureException(ResponseEnum.INVALID_CREDENTIALS, "Invalid credentials")))
        .flatMap(userDto -> {
          User user = User.builder()
              .id(userDto.getId())
              .email(userDto.getEmail())
              .password(userDto.getPassword())
              .role(userDto.getRole())
              .firstname(userDto.getFirstname())
              .lastname(userDto.getLastname())
              .build();

          // Check if passwords match
          if (!jwtService.checkPassword(password, userDto.getPassword())) {
            return Mono.error(
                new GraphqlFailureException(new FailureException(
                    ResponseEnum.INVALID_CREDENTIALS,
                    "Invalid credentials")
                )
            );
          }
          // Generate access and refresh tokens
          String accessToken = jwtService.generateAccessToken(user);
          String refreshToken = jwtService.generateRefreshToken(user);
          UserNoPwdResDTO userResDTO = userMapper.toNoPwdResDTO(user);
          return Mono.just(
              new LoginResDTO(userResDTO, accessToken, refreshToken)
          );
        });
  }

  @Override
  public Mono<Boolean> areTokensValid(String accessToken, String refreshToken) {
    try {
      jwtService.validateAccessToken(accessToken);
    } catch (FailureException ex) {
      if (!ex.getResponseEnum().equals(ResponseEnum.EXPIRED_ACCESS_TOKEN)) {
        return Mono.just(false);
      }
    }
    try {
      jwtService.validateRefreshToken(refreshToken);
    } catch (FailureException ex) {
      return Mono.just(false);
    }
    // Compare issuedAt claim
    Long accessTokenIssueTime = jwtService.getClaimFromAccessToken(accessToken, JwtClaim.ISSUED_AT,
        Long.class);
    Long refreshTokenIssueTime = jwtService.getClaimFromRefreshToken(refreshToken,
        JwtClaim.ISSUED_AT, Long.class);
    if (!accessTokenIssueTime.equals(refreshTokenIssueTime)) {
      return Mono.just(false);
    }
    return Mono.just(true);
  }

  @Override
  public String generateAccessToken(User user) {
    return jwtService.generateAccessToken(user);
  }

  @Override
  public String generateRefreshToken(User user) {
    return jwtService.generateRefreshToken(user);
  }

  @Override
  public Mono<User> getUser(String refreshToken) {
    String id = jwtService.getClaimFromRefreshToken(refreshToken, JwtClaim.ID, String.class);
    String email = jwtService.getClaimFromRefreshToken(refreshToken, JwtClaim.EMAIL, String.class);
    String role = jwtService.getClaimFromRefreshToken(refreshToken, JwtClaim.ROLE, String.class);

    return Mono.just(User.builder()
        .id(id)
        .email(email)
        .role(UserRoleEnum.valueOf(role))
        .build()
    );
  }

}