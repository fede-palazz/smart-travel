package com.certimetergroup.smart_travel.bff_api.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

  @Value("${remote.frontend.url}")
  private String frontendUrl;

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
      ReactiveAuthenticationManager authenticationManager,
      ServerAuthenticationConverter authenticationConverter,
      ServerAuthenticationEntryPoint authenticationEntryPoint) {

    AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(
        authenticationManager);
    authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);

    // Disable login form after authentication exception
    authenticationWebFilter.setAuthenticationFailureHandler(
        new ServerAuthenticationEntryPointFailureHandler(authenticationEntryPoint)
    );

    return http
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers("/graphiql").permitAll()
            .pathMatchers(HttpMethod.POST, "/graphql").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/paypal/**").permitAll()
            .anyExchange().authenticated()
        )
        .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .exceptionHandling(exceptionHandlingSpec ->
            exceptionHandlingSpec.authenticationEntryPoint(authenticationEntryPoint)
        )
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(cors ->
            cors.configurationSource(corsConfigurationSource()))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(frontendUrl));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public ServerAuthenticationEntryPoint authenticationEntryPoint() {
    return (exchange, ex) -> {
      ServerHttpResponse response = exchange.getResponse();
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      String message = ex.getMessage() != null ? ex.getMessage() : "Authentication failed";

      String graphqlError = """
          {
            "data": null,
            "errors": [
              {
                "message": "%s"
              }
            ],
            "extensions": {
              "status": "%s",
              "statusCode": %d
            }
          }
          """.formatted(
          escapeJson(message),
          escapeJson(HttpStatus.UNAUTHORIZED.name()),
          HttpStatus.UNAUTHORIZED.value()
      );

      DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
      return response.writeWith(Mono.just(buffer));
    };
  }

  private String escapeJson(String input) {
    if (input == null) {
      return "";
    }
    return input.replace("\"", "\\\""); // escape quotes if present
  }
}
