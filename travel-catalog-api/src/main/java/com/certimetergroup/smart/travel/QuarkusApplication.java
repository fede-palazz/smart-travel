package com.certimetergroup.smart.travel;

import com.certimetergroup.smart.travel.model.Flight;
import com.mongodb.client.model.Filters;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import shared.Coordinates;
import shared.Country;
import shared.FlightDestination;
import shared.Price;

@Startup
@Slf4j
@ApplicationPath("/api")
public class QuarkusApplication extends Application {

  private final List<Flight> flights = new ArrayList<>();
  private final double flightMinPrice = 35.0;
  private final double flightMaxPrice = 150.0;
  private final int flightMinCapacity = 160;
  private final int flightMaxCapacity = 200;

  /**
   * Get the number of saved flights within the specified month
   *
   * @param month Month used to filter the number of saved flights
   * @return A Uni that resolves to the number of flights in the specified month
   */
  public static Uni<Long> countForMonth(YearMonth month) {
    ZonedDateTime start = month.atDay(1).atStartOfDay(ZoneId.systemDefault());
    ZonedDateTime end = month.plusMonths(1).atDay(1)
        .atStartOfDay(ZoneId.systemDefault()); // exclusive upper bound

    Bson filter = Filters.and(
        Filters.gte("departureTime", start.toInstant()),
        Filters.lt("departureTime", end.toInstant())
    );
    return Flight.count(filter);
  }

  public void onStart(@Observes StartupEvent ev) {
    log.info("Application has started!");
    YearMonth thisMonth = YearMonth.now(ZoneId.of("Europe/Rome"));
    YearMonth nextMonth = YearMonth.now(ZoneId.of("Europe/Rome")).plusMonths(1);

    // Check if the flights collection for next month is empty before inserting data
    log.info("Checking number of saved flights...");

    var thisMonthUni = countForMonth(thisMonth).onItem().transformToUni(count -> {
      log.info("Found {} flights records for this month", count);
      if (count == 0) {
        log.info("Inserting initial flights data for this month...");
        return insertFlights(thisMonth);
      }
      log.info("Flights for current month already exist - skip initialization.");
      return Uni.createFrom().voidItem();  // No operation if data already exists
    });

    var nextMonthUni = countForMonth(nextMonth).onItem().transformToUni(count -> {
      log.info("Found {} flights records for next month", count);
      if (count == 0) {
        log.info("Inserting initial flights data for next month...");
        return insertFlights(nextMonth);
      }
      log.info("Flights for next month already exist - skip initialization.");
      return Uni.createFrom().voidItem();  // No operation if data already exists
    });

    thisMonthUni.chain(() -> nextMonthUni)  // Sequential execution
        .subscribe().with(
            result -> log.info("Initial data insertion process complete."),
            failure -> log.error("Error inserting initial data: ", failure)
        );
  }

  /*********************************************/

  private Uni<Void> insertFlights(YearMonth month) {
    initFlights(month);
    return Flight.persist(flights)
        .onItem().invoke(() -> log.info("Inserted {} flights.", flights.size()))
        .replaceWithVoid();
  }

  private void initFlights(YearMonth month) {
    // Clear flights list
    flights.clear();

    FlightDestination florence = FlightDestination.builder()
        .destinationId(new ObjectId("680f7af8e634eef19a5e09a0"))
        .city("Florence")
        .region("Tuscany")
        .country(new Country("Italy", "IT"))
        .coordinates(new Coordinates(43.7696, 11.2558))
        .timezone("Europe/Rome")
        .airportCode("FLR")
        .airportName("Amerigo Vespucci")
        .build();
    FlightDestination milan = FlightDestination.builder()
        .destinationId(new ObjectId("680f7af8e634eef19a5e09a6"))
        .city("Milan")
        .region("Lombardy")
        .country(new Country("Italy", "IT"))
        .coordinates(new Coordinates(45.4642, 9.19))
        .timezone("Europe/Rome")
        .airportCode("MXP")
        .airportName("Malpensa")
        .build();
    FlightDestination venice = FlightDestination.builder()
        .destinationId(new ObjectId("680f7af8e634eef19a5e09a1"))
        .city("Venice")
        .region("Veneto")
        .country(new Country("Italy", "IT"))
        .coordinates(new Coordinates(45.4408, 12.3155))
        .timezone("Europe/Rome")
        .airportCode("VCE")
        .airportName("Marco Polo")
        .build();
    FlightDestination london = FlightDestination.builder()
        .destinationId(new ObjectId("68401642411ed34eaeaa6a6e"))
        .city("London")
        .region("England")
        .country(new Country("United Kingdom", "GB"))
        .coordinates(new Coordinates(51.509865, -0.118092))
        .timezone("Europe/London")
        .airportCode("LHR")
        .airportName("Heathrow Airport")
        .build();
    FlightDestination paris = FlightDestination.builder()
        .destinationId(new ObjectId("68187dec1e54584b07f2f804"))
        .city("Paris")
        .region("Île-de-France")
        .country(new Country("France", "FR"))
        .coordinates(new Coordinates(48.8566, 2.3522))
        .timezone("Europe/Paris")
        .airportCode("CDG")
        .airportName("Charles de Gaulle")
        .build();

    /**
     * Florence (FLR) --> Milan (MXP)
     */
    Instant[] flightTimes = generateInitialFlightTimes(month, 1, 0);
    flights.add(Flight.builder()
        .airline("ITA Airways")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/commons/7/75/ITA_Airways_Logo.svg")
        .code(Flight.generateRandomFlightCode("AZ"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(florence)
        .to(milan)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Milan (MXP) --> Florence (FLR)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 0);
    flights.add(Flight.builder()
        .airline("ITA Airways")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/commons/7/75/ITA_Airways_Logo.svg")
        .code(Flight.generateRandomFlightCode("AZ"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(florence)
        .to(milan)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Milan (MXP) --> Venice (VCE)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 0);
    flights.add(Flight.builder()
        .airline("ITA Airways")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/commons/7/75/ITA_Airways_Logo.svg")
        .code(Flight.generateRandomFlightCode("AZ"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(milan)
        .to(venice)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Venice (VCE) --> Milan (MXP)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 0);
    flights.add(Flight.builder()
        .airline("ITA Airways")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/commons/7/75/ITA_Airways_Logo.svg")
        .code(Flight.generateRandomFlightCode("AZ"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(venice)
        .to(milan)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Milan (MXP) --> London (LHR)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 50);
    flights.add(Flight.builder()
        .airline("British Airways")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/it/4/42/British_Airways_Logo.svg")
        .code(Flight.generateRandomFlightCode("BA"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(milan)
        .to(london)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Venice (VCE) --> Milan (MXP)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 50);
    flights.add(Flight.builder()
        .airline("British Airways")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/it/4/42/British_Airways_Logo.svg")
        .code(Flight.generateRandomFlightCode("BA"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(london)
        .to(milan)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Milan (MXP) --> Paris (CDG)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 35);
    flights.add(Flight.builder()
        .airline("Air France")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/commons/4/44/Air_France_Logo.svg")
        .code(Flight.generateRandomFlightCode("AF"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(milan)
        .to(paris)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    /**
     * Paris (CDG) --> Milan (MXP)
     */
    flightTimes = generateInitialFlightTimes(month, 1, 35);
    flights.add(Flight.builder()
        .airline("Air France")
        .airlineLogo("https://upload.wikimedia.org/wikipedia/commons/4/44/Air_France_Logo.svg")
        .code(Flight.generateRandomFlightCode("AF"))
        .capacity(ThreadLocalRandom.current().nextInt(flightMinCapacity, flightMaxCapacity))
        .from(paris)
        .to(milan)
        .departureTime(flightTimes[0])
        .arrivalTime(flightTimes[1])
        .price(Price.builder()
            .currency("EUR")
            .value(Math.round(ThreadLocalRandom.current()
                .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0
            )
            .build())
        .build());

    // Spread flights across the month
    flights.addAll(generateMonthlyFlightsFromTemplates(flights, month));
  }

  private List<Flight> generateMonthlyFlightsFromTemplates(List<Flight> initialFlights,
      YearMonth month) {
    LocalDate startDate = month.atDay(3); // assuming initial flights cover days 1–2
    LocalDate endDate = month.atEndOfMonth();

    List<Flight> generatedFlights = new ArrayList<>();

    while (!startDate.isAfter(endDate)) {
      for (Flight baseFlight : initialFlights) {
        long durationMinutes = ChronoUnit.MINUTES.between(
            baseFlight.departureTime,
            baseFlight.arrivalTime
        );
        LocalTime randomTime = LocalTime.of(
            ThreadLocalRandom.current().nextInt(6, 22),
            ThreadLocalRandom.current().nextInt(0, 56)
        );
        LocalDateTime newDeparture = LocalDateTime.of(startDate, randomTime);
        LocalDateTime newArrival = newDeparture.plusMinutes(durationMinutes);

        Flight newFlight = Flight.builder()
            .airline(baseFlight.airline)
            .airlineLogo(baseFlight.airlineLogo)
            .code(Flight.generateRandomFlightCode(
                baseFlight.code.substring(0, 2)
            ))
            .capacity(baseFlight.capacity)
            .from(baseFlight.from)
            .to(baseFlight.to)
            .departureTime(newDeparture
                .atZone(ZoneId.of("Europe/Rome"))
                .toInstant()
            )
            .arrivalTime(newArrival
                .atZone(ZoneId.of("Europe/Rome"))
                .toInstant()
            )
            .price(Price.builder()
                .currency("EUR")
                .value(Math.round(ThreadLocalRandom.current()
                    .nextDouble(flightMinPrice, flightMaxPrice) * 100.0) / 100.0)
                .build())
            .build();

        generatedFlights.add(newFlight);
      }
      // Randomly increment
      startDate = startDate.plusDays(ThreadLocalRandom.current().nextInt(1, 3)); // skip 1–3 days
    }
    return generatedFlights;
  }

  private Instant[] generateInitialFlightTimes(YearMonth month, int durationHours,
      int durationMinutes) {
    LocalDate startOfMonth = month.atDay(1);
    LocalDate randomDay = startOfMonth.plusDays(ThreadLocalRandom.current().nextInt(0, 2));
    LocalTime randomTime = LocalTime.of(
        ThreadLocalRandom.current().nextInt(6, 22),
        ThreadLocalRandom.current().nextInt(0, 59)
    );

    LocalDateTime departure = LocalDateTime.of(randomDay, randomTime);
    LocalDateTime arrival = departure
        .plusHours(durationHours)
        .plusMinutes(durationMinutes);

    Instant departureInstant = departure.atZone(ZoneId.of("Europe/Rome")).toInstant();
    Instant arrivalInstant = arrival.atZone(ZoneId.of("Europe/Rome")).toInstant();

    return new Instant[]{departureInstant, arrivalInstant};
  }


}