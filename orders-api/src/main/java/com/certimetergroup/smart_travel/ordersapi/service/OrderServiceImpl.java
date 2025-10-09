package com.certimetergroup.smart_travel.ordersapi.service;

import com.certimetergroup.smart_travel.ordersapi.dto.request.CancelOrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.request.CaptureOrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.request.OrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.OrderResDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.PagedResDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.PaymentUrlResDTO;
import com.certimetergroup.smart_travel.ordersapi.exception.FailureException;
import com.certimetergroup.smart_travel.ordersapi.exception.ResponseEnum;
import com.certimetergroup.smart_travel.ordersapi.filter.OrderFilter;
import com.certimetergroup.smart_travel.ordersapi.mapper.OrderMapper;
import com.certimetergroup.smart_travel.ordersapi.model.Order;
import com.certimetergroup.smart_travel.ordersapi.model.PayPalOutboxEvent;
import com.certimetergroup.smart_travel.ordersapi.repository.OrderRepository;
import com.certimetergroup.smart_travel.ordersapi.repository.PayPalOutboxRepository;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import shared.order.PaymentStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final PayPalOutboxRepository payPalOutboxRepository;
  private final OrderMapper orderMapper;
  private final PayPalService payPalService;
  private final ReactiveMongoTemplate mongoTemplate;

  @Override
  public Mono<PagedResDTO<OrderResDTO>> getOrders(int page, int size, String sort, String order,
      OrderFilter filters) {
    Sort.Direction direction = order.equalsIgnoreCase("desc") ?
        Sort.Direction.DESC :
        Sort.Direction.ASC;
    Sort sortBy = Sort.by(direction, sort);
    long skip = (long) page * size;

    // Filter query
    Query query = new Query().with(sortBy).skip(skip).limit(size);
    Criteria criteria = new Criteria();

    List<Criteria> criteriaList = new ArrayList<>();

    if (filters.getOrderId() != null && !filters.getOrderId().isBlank()) {
      try {
        criteriaList.add(
            Criteria.where("id").is(new ObjectId(filters.getOrderId()))
        );
      } catch (IllegalArgumentException e) {
        // Invalid ObjectId for orderId, ignore this filter
      }
    }

    if (filters.getCustomerId() != null && !filters.getCustomerId().isBlank()) {
      try {
        criteriaList.add(
            Criteria.where("customerInfo.userId").is(new ObjectId(filters.getCustomerId()))
        );
      } catch (IllegalArgumentException e) {
        // Invalid ObjectId for customerId, ignore this filter
      }
    }

    if (filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
      String regex = "(?i).*" + Pattern.quote(filters.getCustomerName()) + ".*";
      criteriaList.add(new Criteria().orOperator(
          Criteria.where("customerInfo.name").regex(regex),
          Criteria.where("customerInfo.surname").regex(regex)
      ));
    }

    if (filters.getMinAmount() != null) {
      criteriaList.add(Criteria.where("payment.amount.value").gte(filters.getMinAmount()));
    }

    if (filters.getMaxAmount() != null) {
      criteriaList.add(Criteria.where("payment.amount.value").lte(filters.getMaxAmount()));
    }

    if (filters.getStatus() != null) {
      criteriaList.add(Criteria.where("payment.status").is(filters.getStatus()));
    }

    if (filters.getType() != null) {
      criteriaList.add(Criteria.where("type").is(filters.getType().toString()));
    }

    if (!criteriaList.isEmpty()) {
      criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
      query.addCriteria(criteria);
    }

    Mono<List<Order>> contentMono = mongoTemplate.find(query, Order.class).collectList();

    // Count query
    Query countQuery = new Query();
    if (!criteriaList.isEmpty()) {
      countQuery.addCriteria(criteria);
    }
    Mono<Long> totalCountMono = mongoTemplate.count(countQuery, Order.class);

    return Mono.zip(contentMono, totalCountMono)
        .map(tuple -> {
          List<Order> orders = tuple.getT1();
          long totalElements = tuple.getT2();
          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = orders.size();

          return PagedResDTO.<OrderResDTO>builder()
              .content(orders.stream().map(orderMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();

        });
  }

  @Override
  public Mono<OrderResDTO> getOrderById(String id) {
    return getOrderEntityById(id).map(orderMapper::toDto);
  }

  @Override
  public Mono<OrderResDTO> getOrderByToken(String token) {
    return getOrderEntityByToken(token).map(orderMapper::toDto);
  }

  @Override
  public Mono<PaymentUrlResDTO> createOrder(@Valid OrderReqDTO orderReqDTO) {
    // TODO: Check whether there are pending payments
    // Create PayPal order
    var paypalOrder = payPalService.createOrder(
        orderReqDTO.getType().toString(),
        orderReqDTO.getAmount());
    String token = paypalOrder.getId();

    // Create order to save
    Order orderToSave = orderMapper.toEntity(orderReqDTO, token);

    return orderRepository.save(orderToSave)
        .switchIfEmpty(Mono.error(
            new FailureException(
                ResponseEnum.UNEXPECTED_ERROR,
                "Error while creating the order"
            ))
        ).thenReturn(new PaymentUrlResDTO(
            paypalOrder.getLinks().get(1).getHref()
        ));
//        return orderRepository.countPendingOrPaidOrders(orderReqDTO.getCustomerInfo().getUserId())
//                .flatMap(num -> {
//                    return num;
//                })
//                .filter(num ->
//                {
//                    System.out.println(num);
//                    return num.equals(0L);
//                })
//                .switchIfEmpty(Mono.error(new FailureException(
//                        ResponseEnum.ORDER_PENDING,
//                        "Another order is pending")
//                )).flatMap(_ -> {
//                    // Create PayPal order
//                    var paypalOrder = payPalService.createOrder(
//                            orderReqDTO.getType().toString(),
//                            orderReqDTO.getAmount());
//                    String token = paypalOrder.getId();
//
//                    // Create order to save
//                    Order orderToSave = orderMapper.toEntity(orderReqDTO, token);
//
//                    return orderRepository.save(orderToSave)
//                            .switchIfEmpty(Mono.error(
//                                    new FailureException(
//                                            ResponseEnum.UNEXPECTED_ERROR,
//                                            "Error while creating the order"
//                                    ))
//                            ).thenReturn(new PaymentUrlResDTO(
//                                    paypalOrder.getLinks().get(1).getHref()
//                            ));
//                });
  }

  @Override
  public Mono<Void> deleteOrder(String id) {
    return getOrderEntityById(id)
        .flatMap(order ->
            orderRepository.deleteById(order.getId())
        );
  }

  @Override
  @Transactional
  public Mono<Void> captureOrder(@Valid CaptureOrderReqDTO captureReq) {
    return getOrderEntityByToken(captureReq.getToken())
        .flatMap(order -> {
          // Set payment to PAID
          var payment = order.getPayment();
          payment.setStatus(PaymentStatus.PAID);
          payment.setPaidAt(Instant.now());
          payment.setPayerId(captureReq.getPayerId());
          order.setPayment(payment);

          // Create outbox event
          PayPalOutboxEvent outboxEvent = PayPalOutboxEvent.builder()
              .orderId(order.getId())
              .paypalToken(captureReq.getToken())
              .payerId(captureReq.getPayerId())
              .createdAt(Instant.now())
              .build();

          // Save updated order and outbox event
          return orderRepository.save(order)
              .then(payPalOutboxRepository.save(outboxEvent))
              .flatMap(payPalOutboxRepository::delete);
        })
        .then();
  }

  @Override
  @Transactional
  public Mono<Void> cancelOrder(@Valid CancelOrderReqDTO cancelReq) {
    // Set order status to CANCELLED
    return getOrderEntityByToken(cancelReq.getToken())
        .flatMap(order -> {
          // Set payment to CANCELLED
          var payment = order.getPayment();
          payment.setStatus(PaymentStatus.CANCELLED);
          order.setPayment(payment);

          // Save updated order
          return orderRepository.save(order);
        })
        .then();
  }

  @Transactional
  public Mono<Void> setCancelled(String token) {
    return getOrderEntityByToken(token).flatMap(order -> {
      var payment = order.getPayment();
      payment.setStatus(PaymentStatus.CANCELLED);
      order.setPayment(payment);
      return orderRepository.save(order);
    }).then();
  }

  @Transactional
  public Mono<Void> setConfirmed(String token) {
    return getOrderEntityByToken(token).flatMap(order -> {
      var payment = order.getPayment();
      payment.setStatus(PaymentStatus.CONFIRMED);
      order.setPayment(payment);
      return orderRepository.save(order);
    }).then();
  }

  @Transactional
  public Mono<Void> setExpired(String token) {
    return getOrderEntityByToken(token).flatMap(order -> {
      var payment = order.getPayment();
      payment.setStatus(PaymentStatus.EXPIRED);
      order.setPayment(payment);
      return orderRepository.save(order);
    }).then();
  }

  @Override
  public Mono<PaymentStatus> getPaymentStatus(String token) {
    return getOrderEntityByToken(token).flatMap(order ->
        Mono.just(order.getPayment().getStatus())
    );
  }

  /**
   * PRIVATE METHODS
   */

  private Mono<Order> getOrderEntityById(String id) {
    return orderRepository.findById(id)
        .switchIfEmpty(Mono.error(new FailureException(
            ResponseEnum.ORDER_NOT_FOUND,
            "Order with id " + id + " not found")
        ));
  }

  private Mono<Order> getOrderEntityByToken(String token) {
    return orderRepository.findByPayment_Token(token)
        .switchIfEmpty(Mono.error(new FailureException(
            ResponseEnum.ORDER_NOT_FOUND,
            "Order with token " + token + " not found")
        ));
  }
}
