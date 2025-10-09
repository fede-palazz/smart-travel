package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class JacksonConfig {

  @Inject
  public JacksonConfig(ObjectMapper mapper) {
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}

