package com.certimetergroup.smart_travel.bff_api.service;


import com.certimetergroup.smart_travel.bff_api.auth.JwtClaim;
import shared.User;

public interface JwtService {

  String generateAccessToken(User user);

  String generateRefreshToken(User user);

  void validateAccessToken(String accessToken);

  void validateRefreshToken(String refreshToken);

  boolean isAccessTokenExpired(String accessToken);

  <T> T getClaimFromAccessToken(String accessToken, JwtClaim fieldName, Class<T> fieldClass);

  <T> T getClaimFromRefreshToken(String refreshToken, JwtClaim fieldName, Class<T> fieldClass);

  String hashPassword(String password);

  boolean checkPassword(String plainPassword, String hashedPassword);
}
