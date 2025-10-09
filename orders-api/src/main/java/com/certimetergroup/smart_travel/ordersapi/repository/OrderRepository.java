package com.certimetergroup.smart_travel.ordersapi.repository;

import com.certimetergroup.smart_travel.ordersapi.model.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

  @Query(value = "{ 'customerInfo.userId': ?0, 'payment.status': { $in:  ['PENDING', 'PAID']} }", exists = true)
  Mono<Boolean> hasPendingPayment(ObjectId userId);

  @Query("{ 'customerInfo.userId': ?0, 'payment.status': { $in: ['PENDING', 'PAID'] } }")
  Mono<Long> countPendingOrPaidOrders(ObjectId userId);

  Mono<Order> findByPayment_Token(String paymentToken);
}
