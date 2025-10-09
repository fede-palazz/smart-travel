package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.ActivityReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.ActivityResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.filter.ActivityFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

  private final WebClient webClient;

  public ActivityServiceImpl(@Value("${remote.travelcatalog.url}") String baseUrl) {
    webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/activities")
        .build();
  }

  @Override
  public Mono<PagedResDTO<ActivityResDTO>> getActivities(Integer page, Integer size, String sort,
      String order, String timezone, ActivityFilter filters) {
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
              // Description
              if (filters.getDescription() != null) {
                builder.queryParam("description", filters.getDescription());
              }
              // Address
              if (filters.getAddress() != null) {
                builder.queryParam("address", filters.getAddress());
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
              // Tags
              if (filters.getTags() != null) {
                filters.getTags().forEach(tag ->
                    builder.queryParam("tags", tag));
              }
              // Languages
              if (filters.getLanguages() != null) {
                filters.getLanguages().forEach(lang ->
                    builder.queryParam("languages", lang));
              }
              // Min price
              if (filters.getMinPrice() != null) {
                builder.queryParam("minPrice", filters.getMinPrice());
              }
              // Max price
              if (filters.getMaxPrice() != null) {
                builder.queryParam("maxPrice", filters.getMaxPrice());
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
  public Mono<ActivityResDTO> getActivityById(String id) {
    return webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(ActivityResDTO.class);
  }

  @Override
  public Mono<HashSet<ActivityResDTO>> getActivitiesByIds(Set<String> ids) {
    return Flux.fromIterable(ids)
        .flatMap(this::getActivityById)
        .collect(Collectors.toCollection(HashSet::new));
  }

  @Override
  public Mono<ActivityResDTO> addActivity(ActivityReqDTO activityReq) {
    return webClient.post()
        .bodyValue(activityReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(ActivityResDTO.class);
  }

  @Override
  public Mono<ActivityResDTO> updateActivity(String id, ActivityReqDTO activityReq) {
    return webClient.put()
        .uri("/{id}", id)
        .bodyValue(activityReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(ActivityResDTO.class);
  }

  @Override
  public Mono<OkResDTO> deleteActivity(String id) {
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
