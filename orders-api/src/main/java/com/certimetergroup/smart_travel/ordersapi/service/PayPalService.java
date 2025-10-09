package com.certimetergroup.smart_travel.ordersapi.service;

import com.paypal.sdk.models.Order;
import shared.Price;

public interface PayPalService {

  Order createOrder(String description, Price price);

  Order captureOrder(String token, String payerId);
}
