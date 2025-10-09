package com.certimetergroup.smart_travel.ordersapi.config;

import com.rabbitmq.client.ConnectionFactory;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;
import reactor.util.retry.RetrySpec;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbit.url}")
  private String brokerUrl;

  @Value("${rabbit.port}")
  private int brokerPort;

  @Value("${rabbit.username}")
  private String brokerUsername;

  @Value("${rabbit.password}")
  private String brokerPassword;

  @Bean
  public ConnectionFactory connectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(brokerUrl);
    factory.setPort(brokerPort);
    factory.setUsername(brokerUsername);
    factory.setPassword(brokerPassword);
    return factory;
  }

  @Bean
  public Sender sender(ConnectionFactory factory) {
    return RabbitFlux.createSender(new SenderOptions().connectionFactory(factory)
        .connectionMonoConfigurator(cm -> cm.retryWhen(
            RetrySpec.backoff(5, Duration.ofSeconds(3))
        )));
  }

  @Bean
  public Receiver receiver(ConnectionFactory factory) {
    return RabbitFlux.createReceiver(new ReceiverOptions()
        .connectionFactory(factory)
        .connectionMonoConfigurator(cm -> cm.retryWhen(
            RetrySpec.backoff(5, Duration.ofSeconds(3))
        )));
  }
}
