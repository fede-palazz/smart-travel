package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.auth.CustomUserDetails;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.AgencyPackageReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AgencyPackageResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import com.certimetergroup.smart_travel.bff_api.filter.AgencyPackageFilter;
import com.certimetergroup.smart_travel.bff_api.service.AgencyPackageService;
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
import shared.PackageStatus;
import shared.UserRoleEnum;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AgencyPackageController {

  private final AgencyPackageService agencyPackageService;

  @QueryMapping("getAgencyPackages")
  public Mono<PagedResDTO<AgencyPackageResDTO>> agencyPackages(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument AgencyPackageFilter filters,
      @AuthenticationPrincipal Mono<CustomUserDetails> userMono
  ) {
    final AgencyPackageFilter inputFilters = filters != null ? filters : new AgencyPackageFilter();

    // Return only published packages if user is a customer or not logged in
    return userMono
        .map(Optional::of)
        .defaultIfEmpty(Optional.empty())
        .flatMap(user -> {
          // Prevent from showing private packages to unauthorized users
          if (user.isEmpty() || user.get().hasRole(UserRoleEnum.CUSTOMER)) {
            inputFilters.setStatus(PackageStatus.PUBLISHED);
            inputFilters.setAuthorId(null);
          }
          return agencyPackageService.getAgencyPackages(page, size, sort, order, inputFilters);
        });
  }

  @QueryMapping("getAgencyPackageById")
  public Mono<AgencyPackageResDTO> agencyPackageById(
      @Argument String id,
      @AuthenticationPrincipal Mono<CustomUserDetails> userMono
  ) {
    return userMono
        .map(Optional::of)
        .defaultIfEmpty(Optional.empty())
        .flatMap(user -> {
          // Check access to unpublished packages
          return agencyPackageService.getAgencyPackageById(id).flatMap(packageDTO -> {
            boolean isPublished = packageDTO.status().equals(PackageStatus.PUBLISHED);
            boolean isCustomer = user.isEmpty() || user.get().hasRole(UserRoleEnum.CUSTOMER);
            if (!isPublished && isCustomer) {
              return Mono.error(new GraphqlFailureException(
                      new FailureException(ResponseEnum.FORBIDDEN,
                          "You are not allowed to access this resource.")
                  )
              );
            }
            return Mono.just(packageDTO);
          });
        });

  }

  @MutationMapping("addAgencyPackage")
  @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
  public Mono<AgencyPackageResDTO> addActivity(@Argument AgencyPackageReqDTO packageReq) {
    return agencyPackageService.addAgencyPackage(packageReq);
  }

  @MutationMapping("updateAgencyPackage")
  @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
  public Mono<AgencyPackageResDTO> updateAgencyPackage(@Argument String id,
      @Argument AgencyPackageReqDTO packageReq) {
    return agencyPackageService.updateAgencyPackage(id, packageReq);
  }

  @MutationMapping("deleteAgencyPackage")
  @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
  public Mono<OkResDTO> deleteAgencyPackage(@Argument String id) {
    return agencyPackageService.deleteAgencyPackage(id);
  }

  @MutationMapping("publishAgencyPackage")
  @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
  public Mono<OkResDTO> publishAgencyPackage(@Argument String id) {
    return agencyPackageService.publishAgencyPackage(id);
  }

  @MutationMapping("archiveAgencyPackage")
  @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
  public Mono<OkResDTO> archiveAgencyPackage(@Argument String id) {
    return agencyPackageService.archiveAgencyPackage(id);
  }

}
