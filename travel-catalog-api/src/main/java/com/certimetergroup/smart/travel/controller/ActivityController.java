package com.certimetergroup.smart.travel.controller;

import com.certimetergroup.smart.travel.dto.request.ActivityReqDTO;
import com.certimetergroup.smart.travel.dto.response.ActivityResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.exception.ValidationException;
import com.certimetergroup.smart.travel.filter.ActivityFilter;
import com.certimetergroup.smart.travel.service.ActivityService;
import com.certimetergroup.smart.travel.validators.ControllerValidators;
import io.smallrye.mutiny.Uni;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/activities")
@Slf4j
@RequiredArgsConstructor
public class ActivityController {

  private final ActivityService activityService;

  @GET
  public Uni<RestResponse<PagedResDTO<ActivityResDTO>>> getActivities(
      @RestQuery @DefaultValue("10") int size,
      @RestQuery @DefaultValue("0") int page,
      @RestQuery @DefaultValue("name") String sort,
      @RestQuery @DefaultValue("asc") String order,
      @RestQuery @DefaultValue("Europe/Rome") String timezone,
      @BeanParam ActivityFilter filters
  ) {
    var allowedSortValues = List.of("name", "type");
    // Validate query params
    ControllerValidators.validate(size, page, sort, order, allowedSortValues);
    try {
      return activityService.getActivities(page, size, sort, order, timezone, filters).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @GET
  @Path("{id}")
  public Uni<RestResponse<ActivityResDTO>> getActivity(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return activityService.getActivityById(objectId).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }

  @POST
  public Uni<RestResponse<ActivityResDTO>> addActivity(ActivityReqDTO activityReqDTO) {
    try {
      return activityService.addActivity(activityReqDTO).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @PUT
  @Path("{id}")
  public Uni<RestResponse<ActivityResDTO>> updateActivity(@RestPath String id,
      ActivityReqDTO flightReqDTO) {
    try {
      ObjectId objectId = new ObjectId(id);
      return activityService.updateActivity(objectId, flightReqDTO).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @DELETE
  @Path("{id}")
  public Uni<RestResponse<Void>> deleteActivity(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return activityService.deleteActivity(objectId).map(result ->
          RestResponse.status(Response.Status.NO_CONTENT)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }
}
