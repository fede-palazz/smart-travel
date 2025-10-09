package shared;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

  @NotNull(message = "Parameter 'startDate' is required")
  public Instant startDate;     // first occurrence

  @NotNull(message = "Parameter 'endDate' is required")
  public Instant endDate;       // last possible occurrence

  @NotNull(message = "Parameter 'durationMinutes' is required")
  @Positive(message = "Parameter 'durationMinutes' must represent a positive integer")
  public Integer durationMinutes;

  @NotNull(message = "Parameter 'recurrence' is required")
  @Valid
  public ScheduleRecurrence recurrence;
}
