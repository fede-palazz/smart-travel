package shared;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jackson.ObjectIdDeserializer;
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
public class UserSummary {

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  @NotNull(message = "Parameter 'userId' is required")
  private ObjectId userId;

  @NotBlank(message = "Parameter 'email' is required")
  //@Email(message = "Parameter 'email' must represent a valid email")
  private String email;

  @NotBlank(message = "Parameter 'name' is required")
  private String name;

  @NotBlank(message = "Parameter 'surname' is required")
  private String surname;
}
