package com.certimetergroup.smart.travel.service;

import com.certimetergroup.smart.travel.dto.request.AgencyPackageReqDTO;
import com.certimetergroup.smart.travel.dto.response.AgencyPackageResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.filter.AgencyPackageFilter;
import com.certimetergroup.smart.travel.mapper.AgencyPackageMapper;
import com.certimetergroup.smart.travel.model.AgencyPackage;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import shared.PackageStatus;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AgencyPackageService {

  private final AgencyPackageMapper agencyPackageMapper;


  public Uni<PagedResDTO<AgencyPackageResDTO>> getAgencyPackages(
      int page, int size, String sort, String order, @Valid AgencyPackageFilter filters
  ) {
    Bson sortExpr = order.equalsIgnoreCase("desc")
        ? Sorts.descending(sort)
        : Sorts.ascending(sort);

    Bson filterQuery = Filters.and(
        filters.getName() != null ?
            Filters.regex("name", Pattern.compile(filters.getName(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("name"),
        filters.getCity() != null ?
            Filters.regex("destination.city",
                Pattern.compile(filters.getCity(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("destination.city"),
        filters.getRegion() != null ?
            Filters.regex("destination.region",
                Pattern.compile(filters.getRegion(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("destination.region"),
        filters.getCountry() != null ?
            Filters.regex("destination.country.name",
                Pattern.compile(filters.getCountry(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("destination.country.name"),
        (filters.getTags() != null && !filters.getTags().isEmpty()) ?
            Filters.all("tags", filters.getTags()) :
            Filters.exists("tags"),
        filters.getStatus() != null
            ? Filters.eq("status", filters.getStatus().name())
            : Filters.exists("status"),
        // Price range
        filters.getMinPrice() != null ?
            Filters.gte("totalPrice.value", filters.getMinPrice()) :
            Filters.exists("totalPrice.value"),
        filters.getMaxPrice() != null ?
            Filters.lte("totalPrice.value", filters.getMaxPrice()) :
            Filters.exists("totalPrice.value"),
        // Date range
        filters.getStartDate() != null
            ? Filters.gte("endDate", filters.getStartDate())
            : Filters.exists("endDate"),
        filters.getEndDate() != null ?
            Filters.lte("startDate", filters.getEndDate())
            : Filters.exists("startDate"),
        // Author id
        filters.getAuthorId() != null ?
            Filters.eq("agentInfo.userId", new ObjectId(filters.getAuthorId()))
            : Filters.exists("agentInfo.userId")
    );

    ReactivePanacheQuery<AgencyPackage> query = AgencyPackage
        .find(filterQuery, sortExpr)
        .page(Page.of(page, size));

    return Uni.combine().all().unis(query.list(), query.count()).asTuple()
        .map(tuple -> {
          List<AgencyPackage> packages = tuple.getItem1();
          Long totalElements = tuple.getItem2();

          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = packages.size();

          return PagedResDTO.<AgencyPackageResDTO>builder()
              .content(packages.stream().map(agencyPackageMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();
        });
  }

  public Uni<AgencyPackageResDTO> getAgencyPackageById(ObjectId id) {
    return getAgencyPackageEntity(id).map(agencyPackageMapper::toDto);
  }

  public Uni<AgencyPackageResDTO> addAgencyPackage(@Valid AgencyPackageReqDTO agencyPackageReqDTO) {
    AgencyPackage agencyPackage = agencyPackageMapper.toEntity(agencyPackageReqDTO);
    return agencyPackage.<AgencyPackage>persist().map(agencyPackageMapper::toDto)
        .onFailure().transform(t -> {
              log.error("Error: failed to add agency package", t);
              return new FailureException(
                  ResponseEnum.UNEXPECTED_ERROR,
                  "Failed to create the agency package", t
              );
            }
        );
  }

  public Uni<AgencyPackageResDTO> updateAgencyPackage(ObjectId id,
      @Valid AgencyPackageReqDTO agencyPackageReqDTO) {
    return getAgencyPackageEntity(id)
        .onItem().transformToUni(agencyPackage -> {
          agencyPackage.name = agencyPackageReqDTO.getName();
          agencyPackage.description = agencyPackageReqDTO.getDescription();
          agencyPackage.tags = agencyPackageReqDTO.getTags();
          agencyPackage.startDate = agencyPackageReqDTO.getStartDate();
          agencyPackage.endDate = agencyPackageReqDTO.getEndDate();
          agencyPackage.totalPrice = agencyPackageReqDTO.getTotalPrice();
          agencyPackage.destination = agencyPackageReqDTO.getDestination();
          agencyPackage.mainPicture = agencyPackageReqDTO.getMainPicture();
          agencyPackage.pictures = agencyPackageReqDTO.getPictures();
          agencyPackage.departureFlight = agencyPackageReqDTO.getDepartureFlight();
          agencyPackage.returnFlight = agencyPackageReqDTO.getReturnFlight();
          agencyPackage.accommodation = agencyPackageReqDTO.getAccommodation();
          agencyPackage.activities = agencyPackageReqDTO.getActivities();

          return agencyPackage.<AgencyPackage>update()
              .map(agencyPackageMapper::toDto);
        });
  }


  public Uni<AgencyPackageResDTO> publishAgencyPackage(ObjectId id) {
    return getAgencyPackageEntity(id)
        .onItem().transformToUni(agencyPackage -> {
          if (agencyPackage.status.equals(PackageStatus.PUBLISHED)) {
            return Uni.createFrom().failure(() -> new FailureException(
                ResponseEnum.PACKAGE_PUBLISHED,
                String.format("Agency package with id %s is already published", id))
            );
          }
          if (agencyPackage.status.equals(PackageStatus.ARCHIVED)) {
            return Uni.createFrom().failure(() -> new FailureException(
                ResponseEnum.PACKAGE_ARCHIVED,
                String.format("Agency package with id %s is archived and cannot be published", id))
            );
          }
          // Update package status
          agencyPackage.status = PackageStatus.PUBLISHED;
          return agencyPackage.<AgencyPackage>update().map(agencyPackageMapper::toDto);
        });
  }

  public Uni<AgencyPackageResDTO> archiveAgencyPackage(ObjectId id) {
    return getAgencyPackageEntity(id)
        .onItem().transformToUni(agencyPackage -> {
          if (agencyPackage.status == PackageStatus.DRAFT) {
            return Uni.createFrom().failure(() -> new FailureException(
                ResponseEnum.PACKAGE_DRAFT,
                String.format(
                    "Agency package with id %s cannot be archived while it is set as draft", id))
            );
          }
          if (agencyPackage.status == PackageStatus.ARCHIVED) {
            return Uni.createFrom().failure(() -> new FailureException(
                ResponseEnum.PACKAGE_ARCHIVED,
                String.format("Agency package with id %s is already archived", id))
            );
          }
          // Update package status
          agencyPackage.status = PackageStatus.ARCHIVED;
          return agencyPackage.<AgencyPackage>update()
              .map(agencyPackageMapper::toDto);
        });
  }

  public Uni<Void> deleteAgencyPackage(ObjectId id) {
    return getAgencyPackageEntity(id)
        .onItem().transformToUni(agencyPackage ->
            agencyPackage.delete()
        )
        .replaceWithVoid();
  }

  private Uni<AgencyPackage> getAgencyPackageEntity(ObjectId id) {
    return AgencyPackage.<AgencyPackage>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Agency package with id %s not found", id))
        );
  }

}
