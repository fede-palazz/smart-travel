package com.certimetergroup.smart_travel.bff_api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

  private static final String BEARER = "Bearer ";

  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
        .filter(authHeader -> authHeader.startsWith(BEARER))
        .map(authHeader -> authHeader.substring(BEARER.length()))
        .map(JwtToken::new);
  }

}