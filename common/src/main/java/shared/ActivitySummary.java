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
public class ActivitySummary {

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  @NotNull(message = "Parameter 'activityId' is required")
  public ObjectId activityId;

  @NotBlank(message = "Parameter 'name' is required")
  public String name;

  @NotBlank(message = "Parameter 'type' is required")
  public String type;

  @NotBlank(message = "Parameter 'mainPicture' is required")
  public String mainPicture;
}
