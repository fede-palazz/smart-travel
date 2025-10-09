package shared;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRecurrence {

  @NotNull(message = "Parameter 'daysOfWeek' is required")
  public Set<String> daysOfWeek;

  @NotBlank(message = "Parameter 'startTime' is required")
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Parameter 'checkInTime' must be in HH:mm 24-hour format")
  public String startTime;

  @NotBlank(message = "Parameter 'endTime' is required")
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Parameter 'checkOutTime' must be in HH:mm 24-hour format")
  public String endTime;
}
