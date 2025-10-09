package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AccommodationReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AccommodationUpdateReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.filter.AccommodationFilter;
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
public class AccommodationServiceImpl implements AccommodationService {

  private final WebClient webClient;

  public AccommodationServiceImpl(@Value("${remote.travelcatalog.url}") String baseUrl) {
    webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/accommodations")
        .build();
  }

  @Override
  public Mono<PagedResDTO<AccommodationResDTO>> getAccommodations(Integer page, Integer size,
      String sort, String order, String timezone, AccommodationFilter filters) {
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
              // Name
              if (filters.getName() != null) {
                builder.queryParam("name", filters.getName());
              }
              // Types
              if (filters.getTypes() != null) {
                filters.getTypes().forEach(type ->
                    builder.queryParam("types", type));
              }
              // Services
              if (filters.getServices() != null) {
                filters.getServices().forEach(service ->
                    builder.queryParam("services", service));
              }
              // City
              if (filters.getCity() != null) {
                builder.queryParam("city", filters.getCity());
              }
              // Region
              if (filters.getRegion() != null) {
                builder.queryParam("region", filters.getRegion());
              }
              // Country code
              if (filters.getCountry() != null) {
                builder.queryParam("country", filters.getCountry());
              }
              // Address
              if (filters.getAddress() != null) {
                builder.queryParam("address", filters.getAddress());
              }
              // Min distance to center
              if (filters.getMinDistanceToCenterKm() != null) {
                builder.queryParam("minDistanceToCenterKm", filters.getMinDistanceToCenterKm());
              }
              // Max distance to center
              if (filters.getMaxDistanceToCenterKm() != null) {
                builder.queryParam("maxDistanceToCenterKm", filters.getMaxDistanceToCenterKm());
              }
              // Min price per night
              if (filters.getMinPricePerNight() != null) {
                builder.queryParam("minPricePerNight", filters.getMinPricePerNight());
              }
              // Max price per night
              if (filters.getMaxPricePerNight() != null) {
                builder.queryParam("maxPricePerNight", filters.getMaxPricePerNight());
              }
              // Min rating
              if (filters.getMinRating() != null) {
                builder.queryParam("minRating", filters.getMinRating());
              }
              // Start date
              if (filters.getStartDate() != null) {
                builder.queryParam("startDate", filters.getStartDate());
              }
              // End date
              if (filters.getEndDate() != null) {
                builder.queryParam("endDate", filters.getEndDate());
              }
              // Guests
              if (filters.getGuests() != null) {
                builder.queryParam("guests", filters.getGuests());
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

  @Override
  public Mono<AccommodationResDTO> getAccommodationById(String id) {
    return webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AccommodationResDTO.class);
  }

  @Override
  public Mono<AccommodationDetailsResDTO> getAccommodationDetailsById(String id) {
    return webClient.get()
        .uri("/{id}/details", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AccommodationDetailsResDTO.class);
  }

  @Override
  public Mono<AccommodationResDTO> addAccommodation(AccommodationReqDTO accommodationReq) {
    return webClient.post()
        .bodyValue(accommodationReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AccommodationResDTO.class);
  }

  @Override
  public Mono<AccommodationDetailsResDTO> updateAccommodation(String id,
      AccommodationUpdateReqDTO accommodationReq) {
    return webClient.put()
        .uri("/{id}", id)
        .bodyValue(accommodationReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AccommodationDetailsResDTO.class);
  }

  @Override
  public Mono<OkResDTO> deleteAccommodation(String id) {
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
