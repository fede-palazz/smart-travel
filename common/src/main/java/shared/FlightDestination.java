package shared;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlightDestination extends DestinationSummary {

  @NotBlank(message = "Parameter 'airportCode' is required")
  @Size(min = 3, max = 3, message = "Parameter 'airportCode' must be 3 characters long")
  public String airportCode;      // 3-letter IATA code

  @NotBlank(message = "Parameter 'airportName' is required")
  public String airportName;
}
