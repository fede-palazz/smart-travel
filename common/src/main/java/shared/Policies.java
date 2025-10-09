package shared;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policies {

  @NotBlank(message = "Parameter 'cancellation' is required")
  public String cancellation;

  @NotBlank(message = "Parameter 'payment' is required")
  public String payment;

  public String pets;
}
