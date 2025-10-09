package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.ActivityReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.ActivityResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.ActivityFilter;
import com.certimetergroup.smart_travel.bff_api.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ActivityController {

  private final ActivityService activityService;

  @QueryMapping("getActivities")
  public Mono<PagedResDTO<ActivityResDTO>> activities(
      @Argument Integer page, @Argument Integer size, @Argument String sort,
      @Argument String order, @Argument String timezone, @Argument ActivityFilter filters
  ) {
    return activityService.getActivities(page, size, sort, order, timezone, filters);
  }

  @QueryMapping("getActivityById")
  public Mono<ActivityResDTO> activityById(@Argument String id) {
    return activityService.getActivityById(id);
  }

  @MutationMapping("addActivity")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<ActivityResDTO> addActivity(@Argument ActivityReqDTO activityReq) {
    return activityService.addActivity(activityReq);
  }

  @MutationMapping("updateActivity")
  @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
  public Mono<ActivityResDTO> updateActivity(@Argument String id,
      @Argument ActivityReqDTO activityReq) {
    return activityService.updateActivity(id, activityReq);
  }

  @MutationMapping("deleteActivity")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<OkResDTO> deleteActivity(@Argument String id) {
    return activityService.deleteActivity(id);
  }

}
