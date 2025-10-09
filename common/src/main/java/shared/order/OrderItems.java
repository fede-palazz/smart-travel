package shared.order;

import jakarta.validation.Valid;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {

  @Valid
  private FlightOrder departureFlight;

  @Valid
  private FlightOrder returnFlight;

  @Valid
  private AccommodationOrder accommodation;

  @Valid
  private HashSet<ActivityOrder> activities;
}
