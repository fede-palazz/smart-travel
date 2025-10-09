package com.certimetergroup.smart.travel.service;

import com.certimetergroup.smart.travel.dto.request.ActivityReqDTO;
import com.certimetergroup.smart.travel.dto.response.ActivityResDTO;
import com.certimetergroup.smart.travel.dto.response.PagedResDTO;
import com.certimetergroup.smart.travel.exception.FailureException;
import com.certimetergroup.smart.travel.exception.ResponseEnum;
import com.certimetergroup.smart.travel.filter.ActivityFilter;
import com.certimetergroup.smart.travel.mapper.ActivityMapper;
import com.certimetergroup.smart.travel.model.Activity;
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

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ActivityService {

  private final ActivityMapper activityMapper;


  public Uni<PagedResDTO<ActivityResDTO>> getActivities(
      int page, int size, String sort, String order, String timezone, @Valid ActivityFilter filters
  ) {
    System.out.println(filters);
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
        filters.getDescription() != null ?
            Filters.regex("description",
                Pattern.compile(filters.getDescription(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("description"),
        filters.getAddress() != null ?
            Filters.regex("address",
                Pattern.compile(filters.getAddress(), Pattern.CASE_INSENSITIVE)) :
            Filters.exists("address"),
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
            Filters.in("tags", filters.getTags()) :
            Filters.exists("tags"),
        (filters.getLanguages() != null && !filters.getLanguages().isEmpty()) ?
            Filters.in("languages", filters.getLanguages()) :
            Filters.exists("languages"),
        // Price range
        filters.getMinPrice() != null ?
            Filters.gte("price.value", filters.getMinPrice()) :
            Filters.exists("price.value"),
        filters.getMaxPrice() != null ?
            Filters.lte("price.value", filters.getMaxPrice()) :
            Filters.exists("price.value"),
        // Minimum rating
        filters.getMinRating() != null ?
            Filters.gte("reviewsSummary.avgRating", filters.getMinRating()) :
            Filters.exists("reviewsSummary.avgRating"),
        // Date range
        filters.getStartDate() != null
            ? Filters.gte("schedule.endDate", filters.getStartDate().toInstant())
            : Filters.exists("schedule.startDate"),
        filters.getEndDate() != null ?
            Filters.lte("schedule.startDate", filters.getEndDate().toInstant())
            : Filters.exists("schedule.endDate")
    );

    ReactivePanacheQuery<Activity> query = Activity
        .find(filterQuery, sortExpr)
        .page(Page.of(page, size));

    return Uni.combine().all().unis(query.list(), query.count()).asTuple()
        .map(tuple -> {
          List<Activity> activities = tuple.getItem1();
          Long totalElements = tuple.getItem2();

          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = activities.size();

          return PagedResDTO.<ActivityResDTO>builder()
              .content(activities.stream().map(activityMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();
        });
  }

  public Uni<ActivityResDTO> getActivityById(ObjectId id) {
    return Activity.<Activity>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Activity with id %s not found", id))
        )
        .map(activityMapper::toDto);
  }

  public Uni<ActivityResDTO> addActivity(@Valid ActivityReqDTO activityReqDTO) {
    Activity activity = activityMapper.toEntity(activityReqDTO);
    return activity.<Activity>persist().map(activityMapper::toDto)
        .onFailure().transform(t -> {
              log.error("Error: failed to add activity", t);
              return new FailureException(
                  ResponseEnum.UNEXPECTED_ERROR,
                  "Failed to create the activity", t
              );
            }
        );
  }

  public Uni<ActivityResDTO> updateActivity(ObjectId id, @Valid ActivityReqDTO activityReqDTO) {
    return Activity.<Activity>findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Activity with id %s not found", id))
        )
        .onItem().transformToUni(activity -> {
          activity.name = activityReqDTO.getName();
          activity.type = activityReqDTO.getType();
          activity.description = activityReqDTO.getDescription();
          activity.notes = activityReqDTO.getNotes();
          activity.address = activityReqDTO.getAddress();
          activity.coordinates = activityReqDTO.getCoordinates();
          activity.destination = activityReqDTO.getDestination();
          activity.mainPicture = activityReqDTO.getMainPicture();
          activity.pictures = activityReqDTO.getPictures();
          activity.tags = activityReqDTO.getTags();
          activity.languages = activityReqDTO.getLanguages();
          activity.schedule = activityReqDTO.getSchedule();
          activity.price = activityReqDTO.getPrice();

          return activity.<Activity>update()
              .map(activityMapper::toDto);
        });
  }

  public Uni<Void> deleteActivity(ObjectId id) {
    return Activity.findById(id)
        .onItem().ifNull().failWith(() -> new FailureException(
            ResponseEnum.NOT_FOUND,
            String.format("Activity with id %s not found", id))
        )
        .onItem().transformToUni(activity ->
            activity.delete()
        )
        .replaceWithVoid();
  }

}
