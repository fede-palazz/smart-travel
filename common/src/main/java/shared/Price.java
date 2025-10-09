package shared;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import validators.CurrencyCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

  @NotNull(message = "Parameter 'value' is required")
  @PositiveOrZero(message = "Parameter 'value' must be a positive number or zero")
  public Double value;

  @NotBlank(message = "Parameter 'currency' is required")
  @CurrencyCode(message = "Parameter 'currency' must represent a valid currency")
  public String currency;
}
