package validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZoneId;

public class TimeZoneValidator implements ConstraintValidator<TimeZone, String> {

  @Override
  public void initialize(TimeZone constraintAnnotation) {
    // This method can be used to initialize resources or configuration if needed.
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return true;  // Let @NotNull or other annotations handle null values.
    }
    try {
      // Try to get the ZoneId from the string value
      ZoneId zoneId = ZoneId.of(value);
      return true;
    } catch (Exception e) {
      // If ZoneId.of() throws an exception, the timezone string is invalid
      return false;
    }
  }
}
