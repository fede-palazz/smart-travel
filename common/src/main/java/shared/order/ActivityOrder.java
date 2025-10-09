package shared.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jackson.ObjectIdDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import shared.Price;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrder {

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

  @NotNull(message = "Parameter 'date' is required")
  public Instant date;

  @NotNull(message = "Parameter 'quantity' is required")
  public Integer quantity;

  @NotNull(message = "Parameter 'price' is required")
  @Valid
  public Price price;

  @NotBlank(message = "Parameter 'startTime' is required")
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Parameter 'checkInTime' must be in HH:mm 24-hour format")
  public String startTime;

  @NotBlank(message = "Parameter 'endTime' is required")
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Parameter 'checkOutTime' must be in HH:mm 24-hour format")
  public String endTime;
}
