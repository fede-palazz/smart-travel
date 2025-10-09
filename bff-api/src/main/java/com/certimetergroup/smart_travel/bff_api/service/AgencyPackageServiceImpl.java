package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AgencyPackageReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AgencyPackageResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.filter.AgencyPackageFilter;
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
public class AgencyPackageServiceImpl implements AgencyPackageService {

  private final WebClient webClient;

  public AgencyPackageServiceImpl(@Value("${remote.travelcatalog.url}") String baseUrl) {
    webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/agency-packages")
        .build();
  }

  @Override
  public Mono<PagedResDTO<AgencyPackageResDTO>> getAgencyPackages(Integer page, Integer size,
      String sort, String order, AgencyPackageFilter filters) {
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
              // Name
              if (filters.getName() != null) {
                builder.queryParam("name", filters.getName());
              }
              // City
              if (filters.getCity() != null) {
                builder.queryParam("city", filters.getCity());
              }
              // Region
              if (filters.getRegion() != null) {
                builder.queryParam("region", filters.getRegion());
              }
              // Country
              if (filters.getCountry() != null) {
                builder.queryParam("country", filters.getCountry());
              }
              // Tags
              if (filters.getTags() != null) {
                filters.getTags().forEach(tag ->
                    builder.queryParam("tags", tag));
              }
              // Status
              if (filters.getStatus() != null) {
                builder.queryParam("status", filters.getStatus());
              }
              // Min price
              if (filters.getMinPrice() != null) {
                builder.queryParam("minPrice", filters.getMinPrice());
              }
              // Max price
              if (filters.getMaxPrice() != null) {
                builder.queryParam("maxPrice", filters.getMaxPrice());
              }
              // Start date
              if (filters.getStartDate() != null) {
                builder.queryParam("startDate", filters.getStartDate());
              }
              // End date
              if (filters.getEndDate() != null) {
                builder.queryParam("endDate", filters.getEndDate());
              }
              // Author id
              if (filters.getAuthorId() != null) {
                builder.queryParam("authorId", filters.getAuthorId());
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
  public Mono<AgencyPackageResDTO> getAgencyPackageById(String id) {
    return webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AgencyPackageResDTO.class);
  }

  @Override
  public Mono<AgencyPackageResDTO> addAgencyPackage(AgencyPackageReqDTO packageReq) {
    return webClient.post()
        .bodyValue(packageReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AgencyPackageResDTO.class);
  }

  @Override
  public Mono<AgencyPackageResDTO> updateAgencyPackage(String id, AgencyPackageReqDTO packageReq) {
    return webClient.put()
        .uri("/{id}", id)
        .bodyValue(packageReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(AgencyPackageResDTO.class);
  }

  @Override
  public Mono<OkResDTO> deleteAgencyPackage(String id) {
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

  @Override
  public Mono<OkResDTO> publishAgencyPackage(String id) {
    return webClient.post()
        .uri("/{id}/publish", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .toBodilessEntity()
        .thenReturn(new OkResDTO(id));
  }

  @Override
  public Mono<OkResDTO> archiveAgencyPackage(String id) {
    return webClient.post()
        .uri("/{id}/archive", id)
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
