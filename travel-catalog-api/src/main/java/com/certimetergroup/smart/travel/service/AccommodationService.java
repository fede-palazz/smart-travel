package com.certimetergroup.smart.travel.service;

import com.certimetergroup.smart.travel.dto.request.AccommodationReqDTO;
import com.certimetergroup.smart.travel.dto.request.AccommodationUpdateReqDTO;
import com.certimetergroup.smart.travel.dto.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart.travel.dto.response.AccommodationResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.filter.AccommodationFilter;
import com.certimetergroup.smart.travel.mapper.AccommodationMapper;
import com.certimetergroup.smart.travel.model.Accommodation;
import com.certimetergroup.smart.travel.model.AccommodationDetails;
import com.certimetergroup.smart.travel.utils.GeoUtils;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class AccommodationService {

  private final AccommodationMapper accommodationMapper;


  public Uni<PagedResDTO<AccommodationResDTO>> getAccommodations(
      int page, int size, String sort, String order, @Valid AccommodationFilter filters
  ) {
    Bson sortExpr = order.equalsIgnoreCase("desc")
        ? Sorts.descending(sort)
        : Sorts.ascending(sort);

    Bson filterQuery = Filters.and(
        filters.getName() != null ?
            Filters.regex("name", Pattern.compile(filters.getName(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("name"),
        (filters.getTypes() != null && !filters.getTypes().isEmpty()) ?
            Filters.in("type", filters.getTypes()) :
            Filters.exists("type"),
        (filters.getServices() != null && !filters.getServices().isEmpty()) ?
            Filters.all("services", filters.getServices()) :
            Filters.exists("services"),
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
        // Distance to center range
        filters.getMinDistanceToCenterKm() != null ?
            Filters.gte("distanceToCenterKm", filters.getMinDistanceToCenterKm()) :
            Filters.exists("distanceToCenterKm"),
        filters.getMaxDistanceToCenterKm() != null ?
            Filters.lte("distanceToCenterKm", filters.getMaxDistanceToCenterKm()) :
            Filters.exists("distanceToCenterKm"),
        filters.getAddress() != null ?
            Filters.regex("address",
                Pattern.compile(filters.getAddress(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("address"),
        // Price per night range
        (filters.getMinPricePerNight() != null || filters.getMaxPricePerNight() != null
            || filters.getGuests() != null)
            ? Filters.elemMatch("rooms",
            Filters.and(
                filters.getMinPricePerNight() != null
                    ? Filters.gte("pricePerNight.value", filters.getMinPricePerNight())
                    : Filters.exists("pricePerNight.value"),
                filters.getMaxPricePerNight() != null
                    ? Filters.lte("pricePerNight.value", filters.getMaxPricePerNight())
                    : Filters.exists("pricePerNight.value"),
                filters.getGuests() != null
                    ? Filters.gte("capacity", filters.getGuests()) : Filters.exists("capacity")
            )
        ) : Filters.exists("rooms"),
        // Minimum rating
        filters.getMinRating() != null ?
            Filters.gte("reviewsSummary.avgRating", filters.getMinRating()) :
            Filters.exists("reviewsSummary.avgRating")
    );

    ReactivePanacheQuery<AccommodationDetails> query = AccommodationDetails
        .find(filterQuery, sortExpr)
        .page(Page.of(page, size));

    return Uni.combine().all().unis(query.list(), query.count()).asTuple()
        .map(tuple -> {
          List<AccommodationDetails> accommodations = tuple.getItem1();
          Long totalElements = tuple.getItem2();

          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = accommodations.size();

          // Filter rooms based on guests number and price
          accommodations.forEach(acc -> {
            acc.rooms = acc.rooms.stream()
                .filter(room -> {
                      if (filters.getGuests() != null && room.capacity < filters.getGuests()) {
                        return false;
                      }
                      if (filters.getMinPricePerNight() != null
                          && room.pricePerNight.value < filters.getMinPricePerNight()) {
                        return false;
                      }
                      if (filters.getMaxPricePerNight() != null
                          && room.pricePerNight.value > filters.getMaxPricePerNight()) {
                        return false;
                      }
                      return true;
                    }
                )
                .collect(Collectors.toSet());
          });

          return PagedResDTO.<AccommodationResDTO>builder()
              .content(accommodations.stream()
                  .map(accommodationMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();
        });
  }

  public Uni<AccommodationResDTO> getAccommodationById(ObjectId id) {
    return AccommodationDetails
        .find("_id", id)
        .project(Accommodation.class)
        .firstResult()
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Accommodation with id %s not found", id))
        )
        .map(accommodationMapper::toDto);
  }

  public Uni<AccommodationDetailsResDTO> getAccommodationDetailsById(ObjectId id) {
    return AccommodationDetails.<AccommodationDetails>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Accommodation with id %s not found", id))
        )
        .map(accommodationMapper::toDetailsDto);
  }

  public Uni<AccommodationResDTO> addAccommodation(@Valid AccommodationReqDTO accommodationReqDTO) {
    AccommodationDetails accommodation = accommodationMapper.toDetailsEntity(accommodationReqDTO);
    return accommodation.<AccommodationDetails>persist().map(accommodationMapper::toDto)
        .onFailure().transform(t -> {
              log.error("Error: failed to add accommodation", t);
              return new FailureException(
                  ResponseEnum.UNEXPECTED_ERROR,
                  "Failed to create the accommodation", t
              );
            }
        );
  }

  public Uni<AccommodationDetailsResDTO> updateAccommodation(ObjectId id,
      @Valid AccommodationUpdateReqDTO accommodationReqDTO) {
    return AccommodationDetails.<AccommodationDetails>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Accommodation with id %s not found", id))
        ).onItem().transformToUni(acc -> {
          // Update fields
          acc.name = accommodationReqDTO.getName();
          acc.type = accommodationReqDTO.getType();
          acc.address = accommodationReqDTO.getAddress();
          acc.coordinates = accommodationReqDTO.getCoordinates();
          acc.distanceToCenterKm = GeoUtils.haversineDistance(acc.coordinates,
              acc.destination.coordinates);
          acc.mainPicture = accommodationReqDTO.getMainPicture();
          acc.description = accommodationReqDTO.getDescription();
          acc.details = accommodationReqDTO.getDetails();
          acc.checkInTime = accommodationReqDTO.getCheckInTime();
          acc.checkOutTime = accommodationReqDTO.getCheckOutTime();
          acc.contacts = accommodationReqDTO.getContacts();
          acc.policies = accommodationReqDTO.getPolicies();
          acc.pictures = accommodationReqDTO.getPictures();
          acc.services = accommodationReqDTO.getServices();
          acc.languages = accommodationReqDTO.getLanguages();
          acc.rooms = accommodationReqDTO.getRooms();

          return acc.<AccommodationDetails>update()
              .map(accommodationMapper::toDetailsDto);
        });
  }

  public Uni<Void> deleteAccommodation(ObjectId id) {
    return AccommodationDetails.findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Accommodation with id %s not found", id))
        )
        .onItem().transformToUni(accommodation ->
            accommodation.delete()
        )
        .replaceWithVoid();
  }

}
