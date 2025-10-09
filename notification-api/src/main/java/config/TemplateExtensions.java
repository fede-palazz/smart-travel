package config;

import io.quarkus.qute.TemplateExtension;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;

@TemplateExtension
public class TemplateExtensions {

  private static final DateTimeFormatter BASE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  public static String formatDepartureDate(FlightOrder flight) {
    if (flight == null || flight.departureTime == null || flight.from == null
        || flight.from.timezone == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of(flight.from.timezone);
      return BASE_FORMATTER.withZone(zoneId).format(flight.departureTime);
    } catch (DateTimeException e) {
      // Fallback in case timezone is invalid
      return BASE_FORMATTER.withZone(ZoneId.systemDefault()).format(flight.departureTime);
    }
  }

  public static String formatDepartureTime(FlightOrder flight) {
    if (flight == null || flight.departureTime == null || flight.from == null
        || flight.from.timezone == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of(flight.from.timezone);
      return TIME_FORMATTER.withZone(zoneId).format(flight.departureTime);
    } catch (DateTimeException e) {
      return TIME_FORMATTER.withZone(ZoneId.systemDefault()).format(flight.departureTime);
    }
  }

  public static String formatArrivalDate(FlightOrder flight) {
    if (flight == null || flight.arrivalTime == null || flight.to == null
        || flight.to.timezone == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of(flight.to.timezone);
      return BASE_FORMATTER.withZone(zoneId).format(flight.arrivalTime);
    } catch (DateTimeException e) {
      // Fallback in case timezone is invalid
      return BASE_FORMATTER.withZone(ZoneId.systemDefault()).format(flight.arrivalTime);
    }
  }

  public static String formatArrivalTime(FlightOrder flight) {
    if (flight == null || flight.arrivalTime == null || flight.to == null
        || flight.to.timezone == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of(flight.to.timezone);
      return TIME_FORMATTER.withZone(zoneId).format(flight.arrivalTime);
    } catch (DateTimeException e) {
      return TIME_FORMATTER.withZone(ZoneId.systemDefault()).format(flight.arrivalTime);
    }
  }

  public static String formatStartDate(AccommodationOrder accommodation) {
    if (accommodation == null || accommodation.startDate == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of("Europe/Rome");
      return BASE_FORMATTER.withZone(zoneId).format(accommodation.startDate);
    } catch (DateTimeException e) {
      // Fallback in case timezone is invalid
      return BASE_FORMATTER.withZone(ZoneId.systemDefault()).format(accommodation.startDate);
    }
  }

  public static String formatEndDate(AccommodationOrder accommodation) {
    if (accommodation == null || accommodation.endDate == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of("Europe/Rome");
      return BASE_FORMATTER.withZone(zoneId).format(accommodation.endDate);
    } catch (DateTimeException e) {
      // Fallback in case timezone is invalid
      return BASE_FORMATTER.withZone(ZoneId.systemDefault()).format(accommodation.endDate);
    }
  }

  public static String formatDate(ActivityOrder activity) {
    if (activity == null || activity.date == null) {
      return "";
    }
    try {
      ZoneId zoneId = ZoneId.of("Europe/Rome");
      return BASE_FORMATTER.withZone(zoneId).format(activity.date);
    } catch (DateTimeException e) {
      // Fallback in case timezone is invalid
      return BASE_FORMATTER.withZone(ZoneId.systemDefault()).format(activity.date);
    }
  }
}