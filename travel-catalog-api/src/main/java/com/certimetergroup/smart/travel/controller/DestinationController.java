package com.certimetergroup.smart.travel.controller;

import com.certimetergroup.smart.travel.dto.request.DestinationReqDTO;
import com.certimetergroup.smart.travel.dto.response.DestinationResDTO;
import com.certimetergroup.smart.travel.dto.response.DestinationSearchResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.exception.ValidationException;
import com.certimetergroup.smart.travel.filter.DestinationFilter;
import com.certimetergroup.smart.travel.service.DestinationService;
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

@Path("/destinations")
@Slf4j
@RequiredArgsConstructor
public class DestinationController {

  private final DestinationService destinationService;

  @GET
  public Uni<RestResponse<PagedResDTO<DestinationResDTO>>> getDestinations(
      @RestQuery @DefaultValue("10") int size,
      @RestQuery @DefaultValue("0") int page,
      @RestQuery @DefaultValue("city") String sort,
      @RestQuery @DefaultValue("asc") String order,
      @BeanParam DestinationFilter filters
  ) {
    var allowedSortValues = List.of(
        "city",
        "region",
        "description",
        "countryCode",
        "popularityScore"
    );
    // Validate query params
    ControllerValidators.validate(size, page, sort, order, allowedSortValues);
    try {
      return destinationService.getDestinations(page, size, sort, order, filters).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @GET
  @Path("{id}")
  public Uni<RestResponse<DestinationResDTO>> getDestination(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return destinationService.getDestinationById(objectId).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }

  @GET
  @Path("search")
  public Uni<RestResponse<List<DestinationSearchResDTO>>> getDestinationsList(
      @RestQuery String name) {
    return destinationService.getDestinationsList(name).map(result ->
        RestResponse.status(Response.Status.OK, result)
    );
  }

  @POST
  public Uni<RestResponse<DestinationResDTO>> addDestination(DestinationReqDTO destinationReq) {
    try {
      return destinationService.addDestination(destinationReq).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @PUT
  @Path("{id}")
  public Uni<RestResponse<DestinationResDTO>> updateDestination(@RestPath String id,
      DestinationReqDTO destinationReq) {
    try {
      ObjectId objectId = new ObjectId(id);
      return destinationService.updateDestination(objectId, destinationReq).map(result ->
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
  public Uni<RestResponse<Void>> deleteDestination(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return destinationService.deleteDestination(objectId).map(result ->
          RestResponse.status(Response.Status.NO_CONTENT)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }
}
