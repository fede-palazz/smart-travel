package shared.order;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.Price;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  private PaymentStatus status;
  private Instant paidAt;
  private Price amount;
}
