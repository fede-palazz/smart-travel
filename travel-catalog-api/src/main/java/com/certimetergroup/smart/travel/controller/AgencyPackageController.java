package com.certimetergroup.smart.travel.controller;

import com.certimetergroup.smart.travel.dto.request.AgencyPackageReqDTO;
import com.certimetergroup.smart.travel.dto.response.AgencyPackageResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.exception.ValidationException;
import com.certimetergroup.smart.travel.filter.AgencyPackageFilter;
import com.certimetergroup.smart.travel.service.AgencyPackageService;
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

@Path("/agency-packages")
@Slf4j
@RequiredArgsConstructor
public class AgencyPackageController {

  private final AgencyPackageService agencyPackageService;

  @GET
  public Uni<RestResponse<PagedResDTO<AgencyPackageResDTO>>> getAgencyPackages(
      @RestQuery @DefaultValue("10") int size,
      @RestQuery @DefaultValue("0") int page,
      @RestQuery @DefaultValue("creationDate") String sort,
      @RestQuery @DefaultValue("desc") String order,
      @BeanParam AgencyPackageFilter filters
  ) {
    var allowedSortValues = List.of("name", "status", "startDate", "endDate", "creationDate");
    // Validate query params
    ControllerValidators.validate(size, page, sort, order, allowedSortValues);
    try {
      return agencyPackageService.getAgencyPackages(page, size, sort, order, filters).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @GET
  @Path("{id}")
  public Uni<RestResponse<AgencyPackageResDTO>> getAgencyPackage(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return agencyPackageService.getAgencyPackageById(objectId).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }

  @POST
  public Uni<RestResponse<AgencyPackageResDTO>> addAgencyPackage(
      AgencyPackageReqDTO agencyPackageReqDTO) {
    try {
      return agencyPackageService.addAgencyPackage(agencyPackageReqDTO).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @PUT
  @Path("{id}")
  public Uni<RestResponse<AgencyPackageResDTO>> updateAgencyPackage(@RestPath String id,
      AgencyPackageReqDTO agencyPackageReqDTO) {
    try {
      ObjectId objectId = new ObjectId(id);
      return agencyPackageService.updateAgencyPackage(objectId, agencyPackageReqDTO).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @POST
  @Path("{id}/publish")
  public Uni<RestResponse<AgencyPackageResDTO>> publishAgencyPackage(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return agencyPackageService.publishAgencyPackage(objectId).map(result ->
          RestResponse.status(Response.Status.OK, result)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    } catch (ConstraintViolationException ex) {
      throw new ValidationException(ex.getConstraintViolations());
    }
  }

  @POST
  @Path("{id}/archive")
  public Uni<RestResponse<AgencyPackageResDTO>> archiveAgencyPackage(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return agencyPackageService.archiveAgencyPackage(objectId).map(result ->
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
  public Uni<RestResponse<Void>> deleteAgencyPackage(@RestPath String id) {
    try {
      ObjectId objectId = new ObjectId(id);
      return agencyPackageService.deleteAgencyPackage(objectId).map(result ->
          RestResponse.status(Response.Status.NO_CONTENT)
      );
    } catch (IllegalArgumentException ex) {
      throw new FailureException(ResponseEnum.INVALID_PARAM, "Invalid id format");
    }
  }
}
