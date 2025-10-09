package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.FlightReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.FlightResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.filter.FlightFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class FlightServiceImpl implements FlightService {

  private final WebClient webClient;

  public FlightServiceImpl(@Value("${remote.travelcatalog.url}") String baseUrl) {
    webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/flights")
        .build();
  }

  public Mono<PagedResDTO<FlightResDTO>> getFlights(Integer page,
      Integer size,
      String sort,
      String order,
      String timezone,
      FlightFilter filters) {
    return webClient.get().uri(uriBuilder -> {
              UriBuilder builder = uriBuilder
                  .path("")
                  .queryParam("page", page)
                  .queryParam("size", size)
                  .queryParam("sort", sort)
                  .queryParam("order", order)
                  .queryParam("timezone", timezone);
              // Filters
              if (filters == null) {
                return builder.build();
              }
              // Flight code
              if (filters.getCode() != null) {
                builder.queryParam("code", filters.getCode());
              }
              // Airline
              if (filters.getAirline() != null) {
                builder.queryParam("airline", filters.getAirline());
              }
              // Departure city
              if (filters.getFromCity() != null) {
                builder.queryParam("fromCity", filters.getFromCity());
              }
              // Departure region
              if (filters.getFromRegion() != null) {
                builder.queryParam("fromRegion", filters.getFromRegion());
              }
              // Departure country
              if (filters.getFromCountry() != null) {
                builder.queryParam("fromCountry", filters.getFromCountry());
              }
              // Destination city
              if (filters.getToCity() != null) {
                builder.queryParam("toCity", filters.getToCity());
              }
              // Destination region
              if (filters.getToRegion() != null) {
                builder.queryParam("toRegion", filters.getToRegion());
              }
              // Destination country
              if (filters.getToCountry() != null) {
                builder.queryParam("toCountry", filters.getToCountry());
              }
              // Min price
              if (filters.getMinPrice() != null) {
                builder.queryParam("minPrice", filters.getMinPrice());
              }
              // Max price
              if (filters.getMaxPrice() != null) {
                builder.queryParam("maxPrice", filters.getMaxPrice());
              }
              if (filters.getDepartureDate() != null) {
                builder.queryParam("departureDate", filters.getDepartureDate());
              }
              return builder.build();
            }
        )
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(new ParameterizedTypeReference<>() {
        });
  }

  public Mono<FlightResDTO> getFlightById(String id) {
    return webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(FlightResDTO.class);
  }

  @Override
  public Mono<FlightResDTO> addFlight(FlightReqDTO flightReq) {
    return webClient.post()
        .bodyValue(flightReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(FlightResDTO.class);
  }

  @Override
  public Mono<FlightResDTO> updateFlight(String id, FlightReqDTO flightReq) {
    return webClient.put()
        .uri("/{id}", id)
        .bodyValue(flightReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(FlightResDTO.class);
  }

  @Override
  public Mono<OkResDTO> deleteFlight(String id) {
    return webClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .toBodilessEntity()
        .thenReturn(new OkResDTO(id));
  }

}
