package com.certimetergroup.smart_travel.ordersapi.service;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.models.AmountBreakdown;
import com.paypal.sdk.models.AmountWithBreakdown;
import com.paypal.sdk.models.CaptureOrderInput;
import com.paypal.sdk.models.CheckoutPaymentIntent;
import com.paypal.sdk.models.CreateOrderInput;
import com.paypal.sdk.models.Item;
import com.paypal.sdk.models.Money;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderApplicationContext;
import com.paypal.sdk.models.OrderCaptureRequest;
import com.paypal.sdk.models.OrderCaptureRequestPaymentSource;
import com.paypal.sdk.models.OrderRequest;
import com.paypal.sdk.models.PurchaseUnitRequest;
import com.paypal.sdk.models.Token;
import com.paypal.sdk.models.TokenType;
import java.util.Collections;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shared.Price;

@Slf4j
@Service
public class PayPalServiceImpl implements PayPalService {

  private final PaypalServerSdkClient payPalClient;
  private final String returnUrl;
  private final String cancelUrl;
  //private final PayPalOutboxRepository outboxRepository;

  public PayPalServiceImpl(
      PaypalServerSdkClient payPalClient,
      @Value("${paypal.return-url}") String returnUrl,
      @Value("${paypal.cancel-url}") String cancelUrl
      //PayPalOutboxRepository outboxRepository
  ) {
    this.payPalClient = payPalClient;
    this.returnUrl = returnUrl;
    this.cancelUrl = cancelUrl;
    //this.outboxRepository = outboxRepository;
  }

  /**
   * Creates a PayPal order for reservation payment
   */
  public Order createOrder(String description, Price price) {
    OrderRequest orderRequest = new OrderRequest();
    orderRequest.setIntent(CheckoutPaymentIntent.CAPTURE);
    orderRequest.setPurchaseUnits(Collections.singletonList(
        getPurchaseUnitRequest(description, price.getValue(), price.getCurrency())));

    OrderApplicationContext applicationContext = new OrderApplicationContext();
    applicationContext.setReturnUrl(returnUrl);
    applicationContext.setCancelUrl(cancelUrl);

    orderRequest.setApplicationContext(applicationContext);

    CreateOrderInput createOrderInput = new CreateOrderInput();
    createOrderInput.setBody(orderRequest);

    try {
      return payPalClient.getOrdersController().createOrder(createOrderInput).getResult();
    } catch (Exception e) {
      log.error("Failed to create PayPal order", e);
      return null;
    }
  }

  private PurchaseUnitRequest getPurchaseUnitRequest(String description, Double amount,
      String currency) {
    Item item = new Item();
    item.setName(description);
    Money unitAmount = new Money();
    unitAmount.setCurrencyCode(currency);
    unitAmount.setValue(String.format(Locale.US, "%.2f", amount));
    item.setUnitAmount(unitAmount);
    item.setQuantity("1");

    Money itemTotal = new Money();
    itemTotal.setCurrencyCode(currency);
    itemTotal.setValue(String.format(Locale.US, "%.2f", amount));

    AmountBreakdown breakdown = new AmountBreakdown();
    breakdown.setItemTotal(itemTotal);

    AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown();
    amountWithBreakdown.setCurrencyCode(currency);
    amountWithBreakdown.setValue(String.format(Locale.US, "%.2f", amount));
    amountWithBreakdown.setBreakdown(breakdown);

    PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest();
    purchaseUnit.setAmount(amountWithBreakdown);
    purchaseUnit.setItems(Collections.singletonList(item));
    return purchaseUnit;
  }

  /**
   * Captures payment for an approved order
   */
  public Order captureOrder(String token, String payerId) {
    Token paymentToken = new Token(token, TokenType.BILLING_AGREEMENT);
    OrderCaptureRequestPaymentSource paymentSource = new OrderCaptureRequestPaymentSource();
    paymentSource.setToken(paymentToken);

    OrderCaptureRequest captureRequest = new OrderCaptureRequest();
    captureRequest.setPaymentSource(paymentSource);

    CaptureOrderInput captureInput = new CaptureOrderInput();
    captureInput.setBody(captureRequest);
    captureInput.setId(token);

    try {
      return payPalClient.getOrdersController().captureOrder(captureInput).getResult();
    } catch (Exception e) {
      log.error("Failed to create PayPal order", e);
      return null;
    }
  }

//    @Transactional
//    public void createPayPalCaptureEvent(String paypalToken, String payerId, Long paymentId, Long reservationId) {
//        PayPalOutboxEvent event = new PayPalOutboxEvent(paymentId, paypalToken, payerId, reservationId);
//        PayPalOutboxEvent saved = outboxRepository.save(event);
//        outboxRepository.delete(saved);
//    }
}

