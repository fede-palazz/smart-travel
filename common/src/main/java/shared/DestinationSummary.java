package shared;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jackson.ObjectIdDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import validators.TimeZone;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationSummary {

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  @NotNull(message = "Parameter 'destinationId' is required")
  public ObjectId destinationId;

  @NotBlank(message = "Parameter 'city' is required")
  public String city;

  @NotBlank(message = "Parameter 'region' is required")
  public String region;

  @NotNull(message = "Parameter 'country' is required")
  @Valid
  public Country country;

  @NotNull(message = "Parameter 'coordinates' is required")
  @Valid
  public Coordinates coordinates;

  @NotNull(message = "Parameter 'timezone' is required")
  @TimeZone(message = "Parameter 'timezone' must represent a valid timezone")
  public String timezone;
}
