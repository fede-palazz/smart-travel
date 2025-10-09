package com.certimetergroup.smart_travel.ordersapi.model;

import com.certimetergroup.smart_travel.ordersapi.validator.MongoTimestampDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders_outbox_events")
public class PayPalOutboxEvent {

  @Id
  private String id;
  private String orderId;
  private String paypalToken;
  private String payerId;
  @JsonDeserialize(using = MongoTimestampDeserializer.class)
  private Instant createdAt;
}
