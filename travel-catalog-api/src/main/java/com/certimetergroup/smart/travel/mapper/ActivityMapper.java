package com.certimetergroup.smart.travel.mapper;

import com.certimetergroup.smart.travel.dto.request.ActivityReqDTO;
import com.certimetergroup.smart.travel.dto.response.ActivityResDTO;
import com.certimetergroup.smart.travel.model.Activity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import shared.ReviewsSummary;

@Mapper(componentModel = "cdi")
public interface ActivityMapper {

  Activity toEntity(ActivityReqDTO activityReqDTO);

  @AfterMapping
  default void setDefaultReviewsSummary(@MappingTarget Activity activity) {
    activity.reviewsSummary = ReviewsSummary.builder()
        .totalCount(0L)
        .avgRating(0.0)
        .build();
  }

  ActivityResDTO toDto(Activity activity);
}
