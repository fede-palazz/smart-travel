package com.certimetergroup.smart.travel.controller;

import com.certimetergroup.smart.travel.dto.request.AccommodationReqDTO;
import com.certimetergroup.smart.travel.dto.request.AccommodationUpdateReqDTO;
import com.certimetergroup.smart.travel.dto.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart.travel.dto.response.AccommodationResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.exception.ValidationException;
import com.certimetergroup.smart.travel.filter.AccommodationFilter;
import com.certimetergroup.smart.travel.service.AccommodationService;
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

@Path("/accommodations")
@Slf4j
@RequiredArgsConstructor
public class AccommodationController {

  private final AccommodationService accommodationService;

  @GET
  public Uni<RestResponse<PagedResDTO<AccommodationResDTO>>> getAccommodations(
      @RestQuery @DefaultValue("10") int size,
      @RestQuery @DefaultValue("0") int page,
      @RestQuery @DefaultValue("name") String sort,
      @RestQuery @DefaultValue("asc") String order,
      @BeanParam AccommodationFilter filters
  ) {
    var allowedSortValues = List.of("name", "type");
    // Validate query params
    ControllerValidators.validate(size, page, sort, order, allowedSortValues);
    try {
      return accommodationService.getAccommodations(page, size, sort, order, filters).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @GET
  @Path("{id}")
  public Uni<RestResponse<AccommodationResDTO>> getAccommodation(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return accommodationService.getAccommodationById(objectId).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }

  @GET
  @Path("{id}/details")
  public Uni<RestResponse<AccommodationDetailsResDTO>> getAccommodationDetails(
      @RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return accommodationService.getAccommodationDetailsById(objectId).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }

  @POST
  public Uni<RestResponse<AccommodationResDTO>> addAccommodation(
      AccommodationReqDTO accommodationReqDTO) {
    try {
      return accommodationService.addAccommodation(accommodationReqDTO).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @PUT
  @Path("{id}")
  public Uni<RestResponse<AccommodationDetailsResDTO>> updateAccommodation(@RestPath String id,
      AccommodationUpdateReqDTO accommodationUpdateReqDTO) {
    try {
      ObjectId objectId = new ObjectId(id);
      return accommodationService.updateAccommodation(objectId, accommodationUpdateReqDTO)
          .map(result ->
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
      return accommodationService.deleteAccommodation(objectId).map(result ->
          RestResponse.status(Response.Status.NO_CONTENT)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }
}
