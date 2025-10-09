package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.request.UserReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.filter.UserFilter;
import com.certimetergroup.smart_travel.bff_api.mapper.UserMapper;
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
public class UserServiceImpl implements UserService {

  private final WebClient webClient;
  private final UserMapper userMapper;

  public UserServiceImpl(@Value("${remote.user.url}") String baseUrl,
      UserMapper userMapper) {
    webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/users")
        .build();
    this.userMapper = userMapper;
  }

  @Override
  public Mono<PagedResDTO<UserNoPwdResDTO>> getUsers(Integer page, Integer size, String sort,
      String order, UserFilter filters) {
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
              // User ID
              if (filters.getId() != null) {
                builder.queryParam("id", filters.getId());
              }
              // User name
              if (filters.getName() != null) {
                builder.queryParam("name", filters.getName());
              }
              // User email
              if (filters.getEmail() != null) {
                builder.queryParam("email", filters.getEmail());
              }
              // User role
              if (filters.getRole() != null) {
                builder.queryParam("role", filters.getRole());
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
  public Mono<UserNoPwdResDTO> getUserById(String id) {
    return webClient.get()
        .uri("/id/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(UserResDTO.class)
        .map(userMapper::toNoPwdResDTO);
  }

  @Override
  public Mono<UserResDTO> getActualUserById(String id) {
    return webClient.get()
        .uri("/id/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(UserResDTO.class);
  }

  @Override
  public Mono<UserResDTO> getActualUserByEmail(String email) {
    return webClient.get()
        .uri("/email/{email}", email)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(UserResDTO.class);
  }

  @Override
  public Mono<UserNoPwdResDTO> getUserByEmail(String email) {
    return webClient.get()
        .uri("/email/{email}", email)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(UserResDTO.class)
        .map(userMapper::toNoPwdResDTO);
  }

  @Override
  public Mono<List<UserNoPwdResDTO>> getUsersList(String name, boolean excludeCustomers) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/search")
            .queryParam("name", name)
            .queryParam("excludeCustomers", excludeCustomers)
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
  public Mono<UserNoPwdResDTO> addUser(UserReqDTO userReq) {
    return webClient.post()
        .bodyValue(userReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(UserResDTO.class)
        .map(userMapper::toNoPwdResDTO);
  }

  @Override
  public Mono<UserNoPwdResDTO> updateUser(String id, UserReqDTO userReq) {
    return webClient.put()
        .uri("/{id}", id)
        .bodyValue(userReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(UserResDTO.class)
        .map(userMapper::toNoPwdResDTO);
  }

  @Override
  public Mono<OkResDTO> deleteUser(String id) {
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
