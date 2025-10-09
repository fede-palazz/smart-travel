package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.auth.response.LoginResDTO;
import reactor.core.publisher.Mono;
import shared.User;

public interface AuthService {

  Mono<LoginResDTO> login(String username, String password);

  Mono<Boolean> areTokensValid(String accessToken, String refreshToken);

  String generateAccessToken(User user);

  String generateRefreshToken(User user);

  Mono<User> getUser(String token);
}

