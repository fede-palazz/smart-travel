package com.certimetergroup.smart_travel.ordersapi.config;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

  @Value("${paypal.client-id}")
  private String clientId;

  @Value("${paypal.client-secret}")
  private String clientSecret;

  @Bean
  public PaypalServerSdkClient payPalClient() {
    return new PaypalServerSdkClient.Builder()
        .loggingConfig(builder -> builder
            .level(Level.DEBUG)
            .requestConfig(logConfigBuilder -> logConfigBuilder.body(true))
            .responseConfig(logConfigBuilder -> logConfigBuilder.headers(true))
        )
        .httpClientConfig(configBuilder -> configBuilder.timeout(5000)) // 5 seconds timeout
        .clientCredentialsAuth(
            new ClientCredentialsAuthModel.Builder(clientId, clientSecret).build()
        )
        .environment(Environment.SANDBOX)
        .build();
  }
}
