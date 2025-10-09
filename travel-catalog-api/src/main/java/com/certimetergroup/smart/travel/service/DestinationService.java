package com.certimetergroup.smart.travel.service;

import com.certimetergroup.smart.travel.dto.request.DestinationReqDTO;
import com.certimetergroup.smart.travel.dto.response.DestinationResDTO;
import com.certimetergroup.smart.travel.dto.response.DestinationSearchResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.enums.DestinationType;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.filter.DestinationFilter;
import com.certimetergroup.smart.travel.mapper.DestinationMapper;
import com.certimetergroup.smart.travel.model.Destination;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@ApplicationScoped
@RequiredArgsConstructor
public class DestinationService {

  private final DestinationMapper destinationMapper;


  public Uni<PagedResDTO<DestinationResDTO>> getDestinations(
      int page, int size, String sort, String order, @Valid DestinationFilter filters
  ) {
    Bson sortExpr = order.equalsIgnoreCase("desc")
        ? Sorts.descending(sort)
        : Sorts.ascending(sort);

    Bson filterQuery = Filters.and(
        filters.getCity() != null ?
            Filters.regex("city", Pattern.compile(filters.getCity(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("city"),
        filters.getRegion() != null ?
            Filters.regex("region", Pattern.compile(filters.getRegion(), Pattern.CASE_INSENSITIVE))
            :
                Filters.exists("region"),
        filters.getCountryCode() != null ?
            Filters.regex("country.code",
                Pattern.compile(filters.getCountryCode(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("country.code"),
        filters.getMinPopularity() != null ?
            Filters.gte("popularityScore", filters.getMinPopularity()) :
            Filters.exists("popularityScore"),
        filters.getMaxPopularity() != null ?
            Filters.lte("popularityScore", filters.getMaxPopularity()) :
            Filters.exists("popularityScore"),
        (filters.getTags() != null && !filters.getTags().isEmpty()) ?
            Filters.all("tags", filters.getTags()) :
            Filters.exists("tags")
    );

    ReactivePanacheQuery<Destination> query = Destination
        .find(filterQuery, sortExpr)
        .page(Page.of(page, size));

    return Uni.combine().all().unis(query.list(), query.count()).asTuple()
        .map(tuple -> {
          List<Destination> destinations = tuple.getItem1();
          Long totalElements = tuple.getItem2();

          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = destinations.size();

          return PagedResDTO.<DestinationResDTO>builder()
              .content(destinations.stream().map(destinationMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();
        });
  }

  public Uni<DestinationResDTO> getDestinationById(ObjectId id) {
    return Destination.<Destination>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Destination with id %s not found", id))
        )
        .map(destinationMapper::toDto);
  }

  public Uni<List<DestinationSearchResDTO>> getDestinationsList(String name) {
    // Get destinations filtered by city, region or country name
    Bson filters = Filters.or(
        Filters.regex("city",
            Pattern.compile(name, Pattern.CASE_INSENSITIVE)
        ),
        Filters.regex("region",
            Pattern.compile(name, Pattern.CASE_INSENSITIVE)
        ),
        Filters.regex("country.name",
            Pattern.compile(name, Pattern.CASE_INSENSITIVE)
        ));

    ReactivePanacheQuery<Destination> query = Destination.find(filters)
        .page(Page.ofSize(10));

    return query.list()
        .map(destinations -> destinations.stream()
            .flatMap(dest -> {
              List<DestinationSearchResDTO> result = new ArrayList<>();

              // Check if city correspond to name
              if (dest.city.toLowerCase().contains(name.toLowerCase())) {
                result.add(new DestinationSearchResDTO(dest.city, DestinationType.CITY));
              }
              if (dest.region.toLowerCase().contains(name.toLowerCase())) {
                result.add(new DestinationSearchResDTO(dest.region, DestinationType.REGION));
              }
              if (dest.country.name.toLowerCase().contains(name.toLowerCase())) {
                result.add(new DestinationSearchResDTO(dest.country.name, DestinationType.COUNTRY));
              }
              return result.stream();
            })
            // Remove duplicates and maintain order
            .collect(Collectors.toCollection(LinkedHashSet::new))
            .stream()
            .toList()
        );
  }

  public Uni<DestinationResDTO> addDestination(@Valid DestinationReqDTO destinationReq) {
    Destination destination = destinationMapper.toEntity(destinationReq);
    return destination.<Destination>persist().map(destinationMapper::toDto);
  }

  public Uni<DestinationResDTO> updateDestination(ObjectId id,
      @Valid DestinationReqDTO destinationReq) {
    return Destination.<Destination>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Destination with id %s not found", id))
        )
        .onItem().transformToUni(destination -> {
          destination.city = destinationReq.getCity();
          destination.region = destinationReq.getRegion();
          destination.country = destinationReq.getCountry();
          destination.coordinates = destinationReq.getCoordinates();
          destination.description = destinationReq.getDescription();
          destination.pictures = destinationReq.getPictures();
          destination.tags = destinationReq.getTags();
          destination.popularityScore = destinationReq.getPopularityScore();
          destination.timezone = destinationReq.getTimezone();

          return destination.<Destination>update()
              .map(destinationMapper::toDto);
        });
  }

  public Uni<Void> deleteDestination(ObjectId id) {
    return Destination.<Destination>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Destination with id %s not found", id))
        )
        .onItem().transformToUni(destination ->
            destination.delete()
        )
        .replaceWithVoid();
  }
}
