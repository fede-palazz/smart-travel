package com.certimetergroup.smart_travel.ordersapi.config;

import com.certimetergroup.smart_travel.ordersapi.validator.ObjectIdToStringSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public SimpleModule objectIdModule() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(ObjectId.class, new ObjectIdToStringSerializer());
    return module;
  }
}