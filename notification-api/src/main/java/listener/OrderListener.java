package listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.MailingResource;
import dto.OrderNotificationDTO;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@ApplicationScoped
public class OrderListener {

  @Inject
  ObjectMapper mapper;

  @Inject
  ReactiveMailer mailer;


  @Incoming("notification_events")
  public Uni<Void> consume(Message<byte[]> message) {
    OrderNotificationDTO order;
    try {
      order = deserialize(message.getPayload());
      log.info("Received order event: {}", order.id());
    } catch (Exception e) {
      log.error("Failed to deserialize order message", e);
      return Uni.createFrom().completionStage(() -> message.nack(e));
    }

    String customerEmail = order.customerInfo().getEmail();

    // Return html mail template
    return MailingResource.Templates.orderConfirmation(order)
        .to(customerEmail)
        .subject("Order confirmation #" + order.id())
        .send()
        .invoke(() -> log.info("Email sent successfully to {}", customerEmail))
        .onItem().transformToUni(x -> Uni.createFrom().completionStage(message.ack()))
        .onFailure().call(e -> {
          log.error("Email send to {} failed", customerEmail, e);
          return Uni.createFrom().completionStage(() -> message.nack(e));
        });
  }

  private OrderNotificationDTO deserialize(byte[] payload) throws IOException {
    return mapper.readValue(payload, OrderNotificationDTO.class);
  }

}


