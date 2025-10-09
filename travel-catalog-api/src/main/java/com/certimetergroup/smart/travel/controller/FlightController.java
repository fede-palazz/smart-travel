package com.certimetergroup.smart.travel.controller;

import com.certimetergroup.smart.travel.dto.request.FlightReqDTO;
import com.certimetergroup.smart.travel.dto.response.FlightResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.exception.ValidationException;
import com.certimetergroup.smart.travel.filter.FlightFilter;
import com.certimetergroup.smart.travel.service.FlightService;
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

@Path("/flights")
@Slf4j
@RequiredArgsConstructor
public class FlightController {

  private final FlightService flightService;

  @GET
  public Uni<RestResponse<PagedResDTO<FlightResDTO>>> getFlights(
      @RestQuery @DefaultValue("10") int size,
      @RestQuery @DefaultValue("0") int page,
      @RestQuery @DefaultValue("departureTime") String sort,
      @RestQuery @DefaultValue("asc") String order,
      @RestQuery @DefaultValue("Europe/Rome") String timezone,
      @BeanParam FlightFilter filters
  ) {
    var allowedSortValues = List.of(
        "code",
        "capacity",
        "airline",
        "price",
        "departureTime",
        "arrivalTime"
    );
    // Validate query params
    ControllerValidators.validate(size, page, sort, order, allowedSortValues);
    try {
      return flightService.getFlights(page, size, sort, order, timezone, filters).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @GET
  @Path("{id}")
  public Uni<RestResponse<FlightResDTO>> getFlight(@RestPath String id) {
    ObjectId objectId;
    try {
      objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
    return flightService.getFlightById(objectId).map(result ->
        RestResponse.status(Response.Status.OK, result)
    );
  }

  @POST
  public Uni<RestResponse<FlightResDTO>> addFlight(FlightReqDTO flightReqDTO) {
    try {
      return flightService.addFlight(flightReqDTO).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @PUT
  @Path("{id}")
  public Uni<RestResponse<FlightResDTO>> updateFlight(@RestPath String id,
      FlightReqDTO flightReqDTO) {
    try {
      ObjectId objectId = new ObjectId(id);
      return flightService.updateFlight(objectId, flightReqDTO).map(result ->
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
  public Uni<RestResponse<Void>> deleteFlight(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return flightService.deleteFlight(objectId).map(result ->
          RestResponse.status(Response.Status.NO_CONTENT)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }
}
