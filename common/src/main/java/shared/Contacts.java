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
public class Contacts {

  @NotBlank(message = "Parameter 'phone' is required")
  public String phone;

  @NotBlank(message = "Parameter 'email' is required")
  public String email;

  public String website;
}
