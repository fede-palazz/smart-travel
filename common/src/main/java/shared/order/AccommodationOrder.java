package shared.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jackson.ObjectIdDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import shared.Room;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationOrder {

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  @NotNull(message = "Parameter 'accommodationId' is required")
  public ObjectId accommodationId;

  @NotBlank(message = "Parameter 'name' is required")
  public String name;

  @NotBlank(message = "Parameter 'type' is required")
  public String type;

  @NotBlank(message = "Parameter 'mainPicture' is required")
  public String mainPicture;

  @NotNull(message = "Parameter 'rooms' is required")
  @Valid
  public Set<Room> rooms;

  @NotNull(message = "Parameter 'startDate' is required")
  public Instant startDate;

  @NotNull(message = "Parameter 'endDate' is required")
  public Instant endDate;
}
