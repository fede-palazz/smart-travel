package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.request.ActivityReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.ActivityResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.ActivityFilter;
import java.util.HashSet;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface ActivityService {

  Mono<PagedResDTO<ActivityResDTO>> getActivities(Integer page, Integer size, String sort,
      String order, String timezone, ActivityFilter filters);

  Mono<ActivityResDTO> getActivityById(String id);

  Mono<HashSet<ActivityResDTO>> getActivitiesByIds(Set<String> ids);

  Mono<ActivityResDTO> addActivity(ActivityReqDTO activityReq);

  Mono<ActivityResDTO> updateActivity(String id, ActivityReqDTO activityReq);

  Mono<OkResDTO> deleteActivity(String id);

}
