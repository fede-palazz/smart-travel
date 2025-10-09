package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.auth.CustomUserDetails;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.request.UserReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import com.certimetergroup.smart_travel.bff_api.filter.UserFilter;
import com.certimetergroup.smart_travel.bff_api.service.UserService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import shared.UserRoleEnum;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @QueryMapping("getUsers")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<PagedResDTO<UserNoPwdResDTO>> getUsers(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument UserFilter filters
  ) {
    return userService.getUsers(page, size, sort, order, filters);
  }

  @QueryMapping("getUserById")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<UserNoPwdResDTO> getUserById(@Argument String id) {
    return userService.getUserById(id);
  }

  @QueryMapping("getUserByEmail")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<UserNoPwdResDTO> getUserByEmail(@Argument String email) {
    return userService.getUserByEmail(email);
  }

  @QueryMapping("getUsersList")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<List<UserNoPwdResDTO>> getUsersList(
      @Argument String name,
      @Argument Boolean excludeCustomers) {
    if (excludeCustomers != null) {
      return userService.getUsersList(name, excludeCustomers);
    }
    return userService.getUsersList(name, false);
  }

  @MutationMapping("addUser")
  public Mono<UserNoPwdResDTO> addUser(
      @Argument UserReqDTO userReq,
      @AuthenticationPrincipal Mono<CustomUserDetails> userMono
  ) {
    return userMono
        .map(Optional::of)
        .defaultIfEmpty(Optional.empty())
        .flatMap(user -> {
          // Avoid authenticated customers to create a new account
          if (user.isPresent() && user.get().hasRole(UserRoleEnum.CUSTOMER)) {
            return Mono.error(new GraphqlFailureException(
                    new FailureException(ResponseEnum.FORBIDDEN,
                        "You are not allowed to create a new account.")
                )
            );
          }
          return userService.addUser(userReq);
        });
  }

  @MutationMapping("updateUser")
  @PreAuthorize("isAuthenticated()")
  public Mono<UserNoPwdResDTO> updateUser(
      @Argument String id,
      @Argument UserReqDTO userReq,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    // Check user role and permissions
    if (!user.hasRole(UserRoleEnum.ADMIN)) {
      // Check for id match
      if (!Objects.equals(id, user.getId())) {
        return Mono.error(new GraphqlFailureException(
                new FailureException(ResponseEnum.FORBIDDEN,
                    "You are not allowed to modify another account.")
            )
        );
      }
      // Set the request role to the one of the logged-in user
      userReq.setRole(user.getRole());
    }
    return userService.updateUser(id, userReq);
  }

  @MutationMapping("deleteUser")
  @PreAuthorize("isAuthenticated()")
  public Mono<OkResDTO> deleteUser(
      @Argument String id,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    // Check user role and permissions
    if (!user.hasRole(UserRoleEnum.ADMIN)) {
      // Check for id match
      if (!Objects.equals(id, user.getId())) {
        return Mono.error(new GraphqlFailureException(
                new FailureException(ResponseEnum.FORBIDDEN,
                    "You are not allowed to delete another account.")
            )
        );
      }
    }
    return userService.deleteUser(id);
  }

}
