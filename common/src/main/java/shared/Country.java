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
public class Country {

  @NotBlank(message = "Parameter 'name' is required")
  public String name;

  @NotBlank(message = "Parameter 'code' is required")
  public String code;
}
