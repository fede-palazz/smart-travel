package shared;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

  @NotBlank(message = "Parameter 'name' is required")
  public String name;

  @NotBlank(message = "Parameter 'type' is required")
  public String type;

  @NotNull(message = "Parameter 'capacity' is required")
  @Positive(message = "Parameter 'capacity' must be a positive number")
  public Integer capacity;

  @NotNull(message = "Parameter 'pricePerNight' is required")
  @Valid
  public Price pricePerNight;

  @NotNull(message = "Parameter 'quantity' is required")
  @Positive(message = "Parameter 'quantity' must be a positive number")
  public Integer quantity;

  public Set<String> amenities;

  public Set<String> bedTypes;

  public Set<String> pictures;
}
