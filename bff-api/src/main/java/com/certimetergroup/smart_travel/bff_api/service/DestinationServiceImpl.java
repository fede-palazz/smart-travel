package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.DestinationReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.DestinationResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.DestinationSearchResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.filter.DestinationFilter;
import java.util.List;
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
public class DestinationServiceImpl implements DestinationService {

  private final WebClient webClient;

  public DestinationServiceImpl(@Value("${remote.travelcatalog.url}") String baseUrl) {
    webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/destinations")
        .build();
  }

  public Mono<PagedResDTO<DestinationResDTO>> getDestinations(Integer page,
      Integer size,
      String sort,
      String order,
      DestinationFilter filters) {
    return webClient.get().uri(uriBuilder -> {
              UriBuilder builder = uriBuilder
                  .path("")
                  .queryParam("page", page)
                  .queryParam("size", size)
                  .queryParam("sort", sort)
                  .queryParam("order", order);
              // Filters
              if (filters == null) {
                return builder.build();
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
              if (filters.getCountryCode() != null) {
                builder.queryParam("countryCode", filters.getCountryCode());
              }
              // Min popularity score
              if (filters.getMinPopularity() != null) {
                builder.queryParam("minPopularity", filters.getMinPopularity());
              }
              // Max popularity score
              if (filters.getMaxPopularity() != null) {
                builder.queryParam("maxPopularity", filters.getMaxPopularity());
              }
              // Tags
              if (filters.getTags() != null) {
                filters.getTags().forEach(tag ->
                    builder.queryParam("tags", tag));
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

  public Mono<DestinationResDTO> getDestinationById(String id) {
    return webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(DestinationResDTO.class);
  }

  @Override
  public Mono<List<DestinationSearchResDTO>> getDestinationsList(String name) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/search")
            .queryParam("name", name)
            .build()
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
  public Mono<DestinationResDTO> addDestination(DestinationReqDTO destinationReq) {
    return webClient.post()
        .bodyValue(destinationReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(DestinationResDTO.class);
  }

  @Override
  public Mono<DestinationResDTO> updateDestination(String id, DestinationReqDTO destinationReq) {
    return webClient.put()
        .uri("/{id}", id)
        .bodyValue(destinationReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(DestinationResDTO.class);
  }

  @Override
  public Mono<OkResDTO> deleteDestination(String id) {
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
