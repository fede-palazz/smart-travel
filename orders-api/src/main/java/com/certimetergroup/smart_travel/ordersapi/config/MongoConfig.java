package com.certimetergroup.smart_travel.ordersapi.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@AllArgsConstructor
@EnableReactiveMongoRepositories(basePackages = "com.certimetergroup.smart_travel.ordersapi.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

  private final MongoProperties mongoProperties;

  @Override
  public MongoClient reactiveMongoClient() {
    String uri = mongoProperties.getUri(); // Spring pulls this from application.yml
    return MongoClients.create(uri);
  }

  @NotNull
  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDatabase();
  }

  @Bean
  ReactiveMongoTransactionManager transactionManager(
      ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory
  ) {
    return new ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory);
  }

}