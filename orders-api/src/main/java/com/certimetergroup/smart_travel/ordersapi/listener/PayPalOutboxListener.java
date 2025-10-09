package com.certimetergroup.smart_travel.ordersapi.listener;

import com.certimetergroup.smart_travel.ordersapi.mapper.OrderMapper;
import com.certimetergroup.smart_travel.ordersapi.model.PayPalOutboxEvent;
import com.certimetergroup.smart_travel.ordersapi.service.OrderService;
import com.certimetergroup.smart_travel.ordersapi.service.PayPalService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.models.OrderStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayPalOutboxListener {

  private final ObjectMapper objectMapper;
  private final Receiver receiver;
  private final Sender sender;
  private final OrderService orderService;
  private final PayPalService payPalService;
  private final OrderMapper orderMapper;

  @PostConstruct
  public void listenAndForward() {
    receiver.consumeManualAck("orders_outbox_events")
        .flatMap(delivery -> {
          String rawMessage = new String(delivery.getBody());
          // Filter empty values
          if (rawMessage.isBlank() || rawMessage.startsWith("default")) {
            delivery.ack();
            return Mono.empty();
          }
          try {
            JsonNode root = objectMapper.readTree(rawMessage);
            JsonNode payload = root.path("payload");

            // Ignore non-create operations
            String op = payload.path("op").asText();
            if (!"c".equals(op)) {
              delivery.ack();
              return Mono.empty();
            }

            log.info("Received raw message: {}", rawMessage);

            JsonNode after = payload.path("after");

            // Check if payload is present
            if (after.isMissingNode() || after.isNull()) {
              // Reject message
              log.error("Field 'after' is missing or is invalid: {}", payload);
              delivery.ack();
              return Mono.empty();
            }

            JsonNode eventNode = objectMapper.readTree(after.asText());
            PayPalOutboxEvent payPalOutboxEvent = objectMapper.treeToValue(eventNode,
                PayPalOutboxEvent.class);

            // Check if payload is present
            if (payPalOutboxEvent.getOrderId() == null || payPalOutboxEvent.getOrderId()
                .isBlank()) {
              // Reject message
              log.error("Invalid order payload: {}", payload);
              delivery.ack();
              return Mono.empty();
            }
            log.info("Received outbox event for order: {}", payPalOutboxEvent.getOrderId());

            String payerId = payPalOutboxEvent.getPayerId();
            String token = payPalOutboxEvent.getPaypalToken();

            log.info("Token: {}", token);

            return orderService.getPaymentStatus(token).flatMap(status -> {
              switch (status) {
                case CONFIRMED -> {
                  // Order already processed, ack the message
                  delivery.ack();
                  return Mono.empty();
                }
                case PAID -> {
                  return Mono.just(payPalService.captureOrder(token, payerId)).flatMap(order -> {
                    if (order.getStatus().equals(OrderStatus.COMPLETED)) {
                      return orderService.setConfirmed(token)
                          .then(orderService.getOrderByToken(token))
                          .flatMap(updatedOrder -> {
                            try {
                              // Create and send notification
                              String notificationJson = objectMapper.writeValueAsString(
                                  orderMapper.toDTO(updatedOrder)
                              );

                              OutboundMessage notificationEvent = new OutboundMessage(
                                  "notification_events",
                                  "notification_events",
                                  notificationJson.getBytes()
                              );

                              return sender.send(Mono.just(notificationEvent))
                                  .doOnSuccess(ignored -> {
                                    delivery.ack();
                                    log.info("Processed and sent notification for order: {}",
                                        updatedOrder.id());
                                  })
                                  .doOnError(err -> {
                                    delivery.nack(false);
                                    log.error("Failed to send notification: {}", err.getMessage());
                                  })
                                  .onErrorResume(err -> Mono.empty());

                            } catch (Exception e) {
                              delivery.nack(true);
                              log.error("Error processing message: {}", e.getMessage());
                              return Mono.empty();
                            }
                          });
                    } else {
                      // Order capture operation failed
                      return Mono.fromRunnable(() -> {
                        log.error(
                            "Failed to send payment completion event for order {} with status {}",
                            order.getId(), order.getStatus()
                        );
                        delivery.nack(true); // Schedule for retry
                      });
                    }
                  });
                }
                default -> {
                  // Order cancelled or expired
                  log.warn("Cancelling PayPal order event with token {} and status {}", token,
                      status);
                  return orderService.setCancelled(token)
                      .then(Mono.fromRunnable(delivery::ack));
                }
              }
            });
          } catch (Exception e) {
            delivery.nack(false); // Reject malformed messages
            log.error("Error processing message: {}", e.getMessage());
            return Mono.empty();
          }
        })
        .subscribe();
  }
}
