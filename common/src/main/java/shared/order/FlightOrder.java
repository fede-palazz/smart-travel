package shared.order;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jackson.ObjectIdDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import shared.FlightDestination;
import shared.Price;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlightOrder {

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  @NotNull(message = "Parameter 'flightId' is required")
  public ObjectId flightId;

  @NotBlank(message = "Parameter 'code' is required")
  public String code;

  @NotNull(message = "Parameter 'quantity' is required")
  @Positive(message = "Parameter 'quantity' must represent a positive integer")
  public Integer quantity;

  @NotBlank(message = "Parameter 'airline' is required")
  public String airline;

  @NotBlank(message = "Parameter 'airlineLogo' is required")
  public String airlineLogo;

  @NotNull(message = "Parameter 'from' is required")
  @Valid
  public FlightDestination from;

  @NotNull(message = "Parameter 'to' is required")
  @Valid
  public FlightDestination to;

  @NotNull(message = "Parameter 'departureTime' is required")
  public Instant departureTime;

  @NotNull(message = "Parameter 'arrivalTime' is required")
  public Instant arrivalTime;

  @NotNull(message = "Parameter 'price' is required")
  @Valid
  public Price price;
}
