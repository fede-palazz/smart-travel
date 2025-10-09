package shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private String id;
  private String email;
  private String password;
  private UserRoleEnum role;
  private String firstname;
  private String lastname;
}
