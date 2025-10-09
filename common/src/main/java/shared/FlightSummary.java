package shared;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jackson.ObjectIdDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSummary {

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  @NotNull(message = "Parameter 'flightId' is required")
  public ObjectId flightId;

  @NotBlank(message = "Parameter 'code' is required")
  public String code;

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
}
