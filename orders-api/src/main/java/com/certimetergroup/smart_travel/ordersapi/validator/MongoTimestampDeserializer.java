package com.certimetergroup.smart_travel.ordersapi.validator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.Instant;

public class MongoTimestampDeserializer extends JsonDeserializer<Instant> {

  @Override
  public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);

    if (node.has("$date")) {
      JsonNode dateNode = node.get("$date");
      if (dateNode.isNumber()) {
        return Instant.ofEpochMilli(dateNode.asLong());
      } else if (dateNode.isTextual()) {
        return Instant.parse(dateNode.asText()); // ISO 8601
      }
    }

    if (node.has("$numberLong")) {
      return Instant.ofEpochSecond(node.get("$numberLong").asLong());
    }

    throw new JsonParseException(p, "Unsupported timestamp format: " + node);
  }
}
