package com.certimetergroup.smart.travel.service;

import com.certimetergroup.smart.travel.dto.request.FlightReqDTO;
import com.certimetergroup.smart.travel.dto.response.FlightResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.filter.FlightFilter;
import com.certimetergroup.smart.travel.mapper.FlightMapper;
import com.certimetergroup.smart.travel.model.Flight;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@ApplicationScoped
@RequiredArgsConstructor
public class FlightService {

  private final FlightMapper flightMapper;


  public Uni<PagedResDTO<FlightResDTO>> getFlights(
      int page, int size, String sort, String order, String timezone, @Valid FlightFilter filters
  ) {
    // Map nested sort fields to actual fields names
    if (sort.equals("price")) {
      sort = "price.value";
    }
    Bson sortExpr = order.equalsIgnoreCase("desc")
        ? Sorts.descending(sort)
        : Sorts.ascending(sort);

    Bson filterQuery = Filters.and(
        filters.getCode() != null ?
            Filters.regex("code", Pattern.compile(filters.getCode(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("code"),
        filters.getAirline() != null ?
            Filters.regex("airline",
                Pattern.compile(filters.getAirline(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("airline"),
        // Departure
        filters.getFromCity() != null ?
            Filters.regex("from.city",
                Pattern.compile(filters.getFromCity(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("from.city"),
        filters.getFromRegion() != null ?
            Filters.regex("from.region",
                Pattern.compile(filters.getFromRegion(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("from.region"),
        filters.getFromCountry() != null ?
            Filters.regex("from.country.name",
                Pattern.compile(filters.getFromCountry(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("from.country.name"),
        // Destination
        filters.getToCity() != null ?
            Filters.regex("to.city", Pattern.compile(filters.getToCity(), Pattern.CASE_INSENSITIVE))
            :
                Filters.exists("to.city"),
        filters.getToRegion() != null ?
            Filters.regex("to.region",
                Pattern.compile(filters.getToRegion(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("to.region"),
        filters.getToCountry() != null ?
            Filters.regex("to.country.name",
                Pattern.compile(filters.getToCountry(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("to.country.name"),
        // Price range
        filters.getMinPrice() != null ?
            Filters.gte("price.value", filters.getMinPrice()) :
            Filters.exists("price.value"),
        filters.getMaxPrice() != null ?
            Filters.lte("price.value", filters.getMaxPrice()) :
            Filters.exists("price.value")
    );

    // DepartureDate filter
    Bson departureTimeFilter;
    if (filters.getDepartureDate() != null) {
      ZoneId userZone = ZoneId.of(timezone);
      ZonedDateTime departureDate = filters.getDepartureDate(); // UTC
      LocalDate departureLocalDate = departureDate.withZoneSameInstant(userZone).toLocalDate();
      ZonedDateTime now = ZonedDateTime.now(userZone);

      // If departure date is today, consider only flights from current moment
      Instant startOfRange = departureLocalDate.isEqual(now.toLocalDate())
          ? now.toInstant()
          : departureLocalDate.atStartOfDay(userZone).toInstant();

      Instant endOfRange = departureLocalDate.plusDays(1).atStartOfDay(userZone).toInstant();

      departureTimeFilter = Filters.and(
          Filters.gte("departureTime", startOfRange),
          Filters.lt("departureTime", endOfRange)
      );
    } else {
      departureTimeFilter = Filters.exists("departureTime");
    }
    filterQuery = Filters.and(filterQuery, departureTimeFilter);

    ReactivePanacheQuery<Flight> query = Flight
        .find(filterQuery, sortExpr)
        .page(Page.of(page, size));

    return Uni.combine().all().unis(query.list(), query.count()).asTuple()
        .map(tuple -> {
          List<Flight> flights = tuple.getItem1();
          Long totalElements = tuple.getItem2();

          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = flights.size();

          return PagedResDTO.<FlightResDTO>builder()
              .content(flights.stream().map(flightMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();
        });
  }

  public Uni<FlightResDTO> getFlightById(ObjectId id) {
    return Flight.<Flight>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Flight with id %s not found", id))
        )
        .map(flightMapper::toDto);
  }

  public Uni<FlightResDTO> addFlight(@Valid FlightReqDTO flightReqDTO) {
    Flight flight = flightMapper.toEntity(flightReqDTO);
    return flight.<Flight>persist().map(flightMapper::toDto);
  }

  public Uni<FlightResDTO> updateFlight(ObjectId id, @Valid FlightReqDTO flightReqDTO) {
    return Flight.<Flight>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Flight with id %s not found", id))
        )
        .onItem().transformToUni(flight -> {
          flight.code = flightReqDTO.getCode();
          flight.capacity = flightReqDTO.getCapacity();
          flight.airline = flightReqDTO.getAirline();
          flight.airlineLogo = flightReqDTO.getAirlineLogo();
          flight.from = flightReqDTO.getFrom();
          flight.to = flightReqDTO.getTo();
          flight.departureTime = flightReqDTO.getDepartureTime();
          flight.arrivalTime = flightReqDTO.getArrivalTime();
          flight.price = flightReqDTO.getPrice();

          return flight.<Flight>update()
              .map(flightMapper::toDto);
        });
  }

  public Uni<Void> deleteFlight(ObjectId id) {
    return Flight.findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Flight with id %s not found", id))
        )
        .onItem().transformToUni(flight ->
            flight.delete()
        )
        .replaceWithVoid();
  }

}
