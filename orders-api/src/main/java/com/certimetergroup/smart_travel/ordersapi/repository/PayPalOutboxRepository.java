package com.certimetergroup.smart_travel.ordersapi.repository;

import com.certimetergroup.smart_travel.ordersapi.model.PayPalOutboxEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayPalOutboxRepository extends ReactiveMongoRepository<PayPalOutboxEvent, String> {

}
