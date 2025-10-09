package shared;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

  @NotNull(message = "Parameter 'lat' is required")
  public Double lat;

  @NotNull(message = "Parameter 'lng' is required")
  public Double lng;
}
