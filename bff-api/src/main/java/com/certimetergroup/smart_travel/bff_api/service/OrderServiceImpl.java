package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.order.request.CancelOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.CaptureOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.OrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialAccommodationOrder;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialActivityOrder;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialAgencyOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialFlightOrder;
import com.certimetergroup.smart_travel.bff_api.dto.order.request.PartialOrderReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.response.OrderResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.order.response.PaymentUrlResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AccommodationDetailsResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.ActivityResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.AgencyPackageResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.FlightResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.exception.FailureException;
import com.certimetergroup.smart_travel.bff_api.exception.GraphqlFailureException;
import com.certimetergroup.smart_travel.bff_api.exception.ResponseEnum;
import com.certimetergroup.smart_travel.bff_api.filter.OrderFilter;
import com.certimetergroup.smart_travel.bff_api.mapper.OrderMapper;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import shared.PackageStatus;
import shared.Price;
import shared.UserSummary;
import shared.order.AccommodationOrder;
import shared.order.ActivityOrder;
import shared.order.FlightOrder;
import shared.order.OrderItems;
import shared.order.OrderType;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

  private final FlightService flightService;
  private final AccommodationService accommodationService;
  private final UserService userService;
  private final WebClient webClient;
  private final OrderMapper orderMapper;
  private final ActivityService activityService;
  private final AgencyPackageService agencyPackageService;

  public OrderServiceImpl(
      @Value("${remote.order.url}") String baseUrl,
      FlightService flightService,
      AccommodationService accommodationService,
      UserService userService,
      OrderMapper orderMapper,
      ActivityService activityService,
      AgencyPackageService agencyPackageService
  ) {
    this.flightService = flightService;
    this.accommodationService = accommodationService;
    this.userService = userService;
    this.orderMapper = orderMapper;
    this.activityService = activityService;
    this.agencyPackageService = agencyPackageService;

    this.webClient = WebClient.builder()
        .baseUrl(baseUrl + "/api/orders")
        .build();
  }

  @Override
  public Mono<PagedResDTO<OrderResDTO>> getOrders(Integer page, Integer size, String sort,
      String order, OrderFilter filters) {
    return webClient.get().uri(uriBuilder -> {
              UriBuilder builder = uriBuilder
                  .path("")
                  .queryParam("page", page)
                  .queryParam("size", size)
                  .queryParam("sort", sort)
                  .queryParam("order", order);
              // Filters
              if (filters == null) {
                return builder.build();
              }
              // Order ID
              if (filters.getOrderId() != null) {
                builder.queryParam("orderId", filters.getOrderId());
              }
              // Customer ID
              if (filters.getCustomerId() != null) {
                builder.queryParam("customerId", filters.getCustomerId());
              }
              // Customer name or surname or both
              else if (filters.getCustomerName() != null) {
                builder.queryParam("customerName", filters.getCustomerName());
              }
              // Min amount
              if (filters.getMinAmount() != null) {
                builder.queryParam("minAmount", filters.getMinAmount());
              }
              // Max amount
              if (filters.getMaxAmount() != null) {
                builder.queryParam("maxAmount", filters.getMaxAmount());
              }
              // Status
              if (filters.getStatus() != null) {
                builder.queryParam("status", filters.getStatus());
              }
              // Type
              if (filters.getType() != null) {
                builder.queryParam("type", filters.getType());
              }
              return builder.build();
            }
        )
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(new ParameterizedTypeReference<>() {
        });
  }

  @Override
  public Mono<OrderResDTO> getOrderById(String id) {
    return webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(OrderResDTO.class);
  }

  @Override
  public Mono<PaymentUrlResDTO> createOrder(PartialOrderReqDTO partialOrderReq, String userId) {
    PartialFlightOrder departureFlightOrderReq = partialOrderReq.getDepartureFlight();
    PartialFlightOrder returnFlightOrderReq = partialOrderReq.getReturnFlight();
    PartialAccommodationOrder accommodationOrderReq = partialOrderReq.getAccommodation();
    HashSet<PartialActivityOrder> activityOrdersReq = partialOrderReq.getActivities();

    Mono<UserNoPwdResDTO> customer$ = userService.getUserById(userId);

    Mono<Optional<FlightResDTO>> departureFlight$ = departureFlightOrderReq != null ?
        flightService.getFlightById(departureFlightOrderReq.getFlightId()).map(Optional::of) :
        Mono.just(Optional.empty());

    Mono<Optional<FlightResDTO>> returnFlight$ = returnFlightOrderReq != null ?
        flightService.getFlightById(returnFlightOrderReq.getFlightId()).map(Optional::of) :
        Mono.just(Optional.empty());

    Mono<Optional<AccommodationDetailsResDTO>> accommodation$ = accommodationOrderReq != null ?
        accommodationService.getAccommodationDetailsById(accommodationOrderReq.getAccommodationId())
            .map(Optional::of) :
        Mono.just(Optional.empty());

    Mono<Optional<HashSet<ActivityResDTO>>> activities$ =
        activityOrdersReq != null && !activityOrdersReq.isEmpty()
            ? activityService.getActivitiesByIds(
                activityOrdersReq.stream().map(PartialActivityOrder::getActivityId)
                    .collect(Collectors.toSet()))
            .map(Optional::of)
            : Mono.just(Optional.empty());

    return Mono.zip(customer$, departureFlight$, returnFlight$, accommodation$, activities$)
        .flatMap(tuple -> {
          UserNoPwdResDTO customer = tuple.getT1();
          Optional<FlightResDTO> departureFlight = tuple.getT2();
          Optional<FlightResDTO> returnFlight = tuple.getT3();
          Optional<AccommodationDetailsResDTO> accommodation = tuple.getT4();
          Optional<HashSet<ActivityResDTO>> activities = tuple.getT5();

          // Gather customer info
          UserSummary customerInfo = UserSummary.builder()
              .name(customer.getFirstname())
              .surname(customer.getLastname())
              .userId(new ObjectId(customer.getId()))
              .email(customer.getEmail())
              .build();

          // Set flights order
          FlightOrder departureFlightOrder = departureFlight.map(flightResDTO ->
              orderMapper.toOrder(flightResDTO, departureFlightOrderReq.getQuantity())
          ).orElse(null);
          FlightOrder returnFlightOrder = returnFlight.map(flightResDTO ->
              orderMapper.toOrder(flightResDTO, returnFlightOrderReq.getQuantity())
          ).orElse(null);

          // Set accommodation order
          AccommodationOrder accommodationOrder = accommodation.map(accommodationResDTO ->
              orderMapper.toOrder(accommodationResDTO,
                  accommodationOrderReq.getStartDate(),
                  accommodationOrderReq.getEndDate(),
                  accommodationOrderReq.getRooms() // TODO: Check for rooms validity
              )
          ).orElse(null);

          // Set activities order
          HashSet<ActivityOrder> activitiesOrder = activities.map(
                  activitiesResDTOs -> activitiesResDTOs.stream().map(activityResDTO -> {
                        // Find the matching partial order to supply date and quantity
                        PartialActivityOrder match = activityOrdersReq.stream().filter(p ->
                                p.getActivityId().equals(activityResDTO.id())
                            )
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(
                                "Missing data for activityId: " + activityResDTO.id())
                            );
                        // Convert to ActivityOrder using the mapper
                        return orderMapper.toOrder(
                            activityResDTO,
                            match.getDate(),
                            match.getQuantity()
                        );
                      })
                      .collect(Collectors.toCollection(HashSet::new))
              )
              .orElse(null);

          OrderItems orderItems = OrderItems
              .builder()
              .departureFlight(departureFlightOrder)
              .returnFlight(returnFlightOrder)
              .accommodation(accommodationOrder)
              .activities(activitiesOrder)
              .build();

          // Set total amount
          double amount = 0.0;
          int selectedItemCount = 0;  // Used to determine the order type

          if (departureFlightOrder != null) {
            amount +=
                departureFlightOrder.getPrice().getValue() * departureFlightOrder.getQuantity();

            if (returnFlightOrder != null) {
              amount += returnFlightOrder.getPrice().getValue() * returnFlightOrder.getQuantity();
            }
            selectedItemCount++;
          }

          if (accommodationOrder != null) {
            double roomsTotal = accommodationOrder.getRooms().stream()
                .map(room ->
                    room.getPricePerNight().getValue() * room.getQuantity()
                )
                .reduce(0.0, Double::sum);
            long numDays = ChronoUnit.DAYS.between(
                accommodationOrder.getStartDate().atZone(ZoneOffset.UTC).toLocalDate(),
                accommodationOrder.getEndDate().atZone(ZoneOffset.UTC).toLocalDate()
            );
            amount += roomsTotal * numDays;
            selectedItemCount++;
          }
          if (activitiesOrder != null && !activitiesOrder.isEmpty()) {
            amount += activitiesOrder.stream()
                .mapToDouble(activityOrder ->
                    activityOrder.getPrice().getValue() * activityOrder.getQuantity())
                .sum();
            selectedItemCount++;
          }

          // Set order type
          OrderType orderType = selectedItemCount > 1 ? OrderType.CUSTOM : OrderType.SINGLE;

          OrderReqDTO orderReq = OrderReqDTO
              .builder()
              .customerInfo(customerInfo)
              .type(orderType)
              .items(orderItems)
              .amount(new Price(amount, "EUR"))
              .build();

          return placePayPalOrder(orderReq);
        });
  }

  @Override
  public Mono<PaymentUrlResDTO> createAgencyOrder(
      PartialAgencyOrderReqDTO partialAgencyOrderReq,
      String userId
  ) {
    String agencyPackageId = partialAgencyOrderReq.getAgencyPackageId();
    Mono<AgencyPackageResDTO> agencyPackage$ = agencyPackageService.getAgencyPackageById(
        agencyPackageId
    );
    Mono<UserNoPwdResDTO> customer$ = userService.getUserById(userId);
    return Mono.zip(agencyPackage$, customer$)
        .flatMap(tuple -> {
          AgencyPackageResDTO agencyPackage = tuple.getT1();
          UserNoPwdResDTO customer = tuple.getT2();

          // Ensure that agency package has been published
          if (!agencyPackage.status().equals(PackageStatus.PUBLISHED)) {
            return Mono.error(new GraphqlFailureException(
                new FailureException(ResponseEnum.FORBIDDEN,
                    "You are not allowed to access this resource."
                ))
            );
          }

          // Gather customer info
          UserSummary customerInfo = UserSummary.builder()
              .name(customer.getFirstname())
              .surname(customer.getLastname())
              .userId(new ObjectId(customer.getId()))
              .email(customer.getEmail())
              .build();

          OrderItems orderItems = OrderItems
              .builder()
              .departureFlight(agencyPackage.departureFlight())
              .returnFlight(agencyPackage.returnFlight())
              .accommodation(agencyPackage.accommodation())
              .activities(new HashSet<>(agencyPackage.activities()))
              .build();

          OrderReqDTO orderReq = OrderReqDTO
              .builder()
              .customerInfo(customerInfo)
              .type(OrderType.AGENCY)
              .items(orderItems)
              .amount(new Price(
                      agencyPackage.totalPrice().value,
                      agencyPackage.totalPrice().currency
                  )
              )
              .agencyPackageId(agencyPackageId)
              .build();

          return placePayPalOrder(orderReq);
        });
  }

  private Mono<PaymentUrlResDTO> placePayPalOrder(OrderReqDTO orderReq) {
    return webClient.post()
        .bodyValue(orderReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(ProblemDetail.class).flatMap(problem ->
                Mono.error(new GraphqlFailureException(problem))
            )
        )
        .bodyToMono(PaymentUrlResDTO.class);
  }

  @Override
  public Mono<Void> captureOrder(String token, String payerId) {
    return webClient.post()
        .uri("/capture")
        .bodyValue(new CaptureOrderReqDTO(token, payerId))
        .retrieve()
        .bodyToMono(Void.class);
  }

  @Override
  public Mono<Void> cancelOrder(String token) {
    return webClient.post()
        .uri("/cancel")
        .bodyValue(new CancelOrderReqDTO(token))
        .retrieve()
        .bodyToMono(Void.class);
  }
}
