package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.auth.JwtClaim;
import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import shared.User;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

  private static final MacAlgorithm signatureAlgorithm = Jwts.SIG.HS512;

  // https://www.baeldung.com/spring-classpath-file-access#2-using-value
  @Value("classpath:jwt/access_token.key")
  private Resource ACCESS_TOKEN_KEY_FILE;
  @Value("classpath:jwt/refresh_token.key")
  private Resource REFRESH_TOKEN_KEY_FILE;
  @Value("${authentication.jwt.access_token.expiration.time}")
  private long ACCESS_TOKEN_EXPIRATION_TIME_MILLISECS;
  @Value("${authentication.jwt.refresh_token.expiration.time}")
  private long REFRESH_TOKEN_EXPIRATION_TIME_MILLISECS;

  //--------------------
  private SecretKey accessTokenKey;
  private SecretKey refreshTokenKey;
  //--------------------

  @PostConstruct
  public void init() throws IOException {
    this.refreshTokenKey = Keys.hmacShaKeyFor(REFRESH_TOKEN_KEY_FILE.getContentAsByteArray());
    this.accessTokenKey = Keys.hmacShaKeyFor(ACCESS_TOKEN_KEY_FILE.getContentAsByteArray());
  }

  public String generateRefreshToken(User user) {
    Date now = new Date(System.currentTimeMillis());
    Date expiration = new Date(
        REFRESH_TOKEN_EXPIRATION_TIME_MILLISECS + System.currentTimeMillis());
    return Jwts
        .builder()
        .signWith(refreshTokenKey, signatureAlgorithm)
        .issuedAt(now)
        .expiration(expiration)
        .claim(JwtClaim.ID.getDescription(), user.getId())
        .claim(JwtClaim.EMAIL.getDescription(), user.getEmail())
        .claim(JwtClaim.ROLE.getDescription(), user.getRole().name())
        .compact();
  }

  public String generateAccessToken(User user) {
    Date now = new Date(System.currentTimeMillis());
    Date expiration = new Date(ACCESS_TOKEN_EXPIRATION_TIME_MILLISECS + System.currentTimeMillis());
    return Jwts
        .builder()
        .signWith(accessTokenKey, signatureAlgorithm)
        .issuedAt(now)
        .expiration(expiration)
        .claim(JwtClaim.ID.getDescription(), user.getId())
        .claim(JwtClaim.EMAIL.getDescription(), user.getEmail())
        .claim(JwtClaim.ROLE.getDescription(), user.getRole().name())
        .compact();
  }

  public void validateAccessToken(String accessToken) {
    try {
      this.validateToken(accessTokenKey, accessToken);
    } catch (ExpiredJwtException e) {
      log.warn("EXPIRED ACCESS TOKEN: {}", accessToken);
      throw new FailureException(ResponseEnum.EXPIRED_ACCESS_TOKEN, "Access token expired");
    } catch (SignatureException e) {
      log.warn("INVALID ACCESS TOKEN: {}", accessToken);
      throw new FailureException(ResponseEnum.INVALID_ACCESS_TOKEN, "Invalid access token");
    } catch (MalformedJwtException e) {
      log.error("MALFORMED ACCESS TOKEN: {}", accessToken);
      throw new FailureException(ResponseEnum.MALFORMED_ACCESS_TOKEN, "Malformed access token");
    } catch (Exception e) {
      log.error("UNEXPECTED ERROR", e);
      throw new FailureException(ResponseEnum.UNEXPECTED_ERROR, "Unexpected error");
    }
  }

  public void validateRefreshToken(String refreshToken) {
    try {
      this.validateToken(refreshTokenKey, refreshToken);
    } catch (ExpiredJwtException e) {
      log.warn("EXPIRED REFRESH TOKEN: {}", refreshToken);
      throw new FailureException(ResponseEnum.EXPIRED_REFRESH_TOKEN, "Refresh token expired");
    } catch (SignatureException e) {
      log.warn("INVALID REFRESH TOKEN: {}", refreshToken);
      throw new FailureException(ResponseEnum.INVALID_REFRESH_TOKEN, "Invalid refresh token");
    } catch (MalformedJwtException e) {
      log.error("MALFORMED REFRESH TOKEN: {}", refreshToken);
      throw new FailureException(ResponseEnum.MALFORMED_REFRESH_TOKEN, "Malformed refresh token");
    } catch (Exception e) {
      log.error("UNEXPECTED ERROR: {}", e.getMessage());
      throw new FailureException(ResponseEnum.UNEXPECTED_ERROR, "Unexpected error");
    }
  }

  public <T> T getClaimFromAccessToken(String accessToken, JwtClaim fieldName,
      Class<T> fieldClass) {
    return this.getClaimFromToken(accessTokenKey, accessToken, fieldName.getDescription(),
        fieldClass);
  }

  public <T> T getClaimFromRefreshToken(String refreshToken, JwtClaim fieldName,
      Class<T> fieldClass) {
    return this.getClaimFromToken(refreshTokenKey, refreshToken, fieldName.getDescription(),
        fieldClass);
  }

  public boolean isAccessTokenExpired(String accessToken) {
    try {
      validateAccessToken(accessToken);
      return false;       // token valid
    } catch (FailureException e) {
      if (e.getResponseEnum().equals(ResponseEnum.EXPIRED_ACCESS_TOKEN)) {
        return true;    // token expired
      }
      throw e;            // token malformed --> ERROR
    }
  }

  // Hash a password using BCrypt
  public String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt(12));
  }

  // Check if a plain password matches the hashed password
  public boolean checkPassword(String plainPassword, String hashedPassword) {
    return BCrypt.checkpw(plainPassword, hashedPassword);
  }

  /**************************************************************************************/

  private void validateToken(SecretKey secretKey, String token) {
    Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
  }

  private <T> T getClaimFromToken(SecretKey secretKey, String token, String fieldName,
      Class<T> fieldClass) {
    try {
      Jws<Claims> claims = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);

      return claims.getPayload().get(fieldName, fieldClass);

    } catch (ExpiredJwtException ex) {
      // Use the claims from the exception if expired but trusted
      Claims claims = ex.getClaims();
      return claims.get(fieldName, fieldClass);

    }
  }

}
