package com.certimetergroup.smart_travel.bff_api.controller;

import com.certimetergroup.smart_travel.bff_api.service.OrderService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import shared.order.PaymentStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paypal")
public class PayPalController {

  private final OrderService orderService;

  @Value("${remote.frontend.redirect_url}")
  private String frontendUrl;

  @GetMapping("/capture")
  public Mono<Void> capturePayPalOrder(
      @RequestParam("token") String token,
      @RequestParam("PayerID") String payerID,
      ServerHttpResponse response
  ) {
    log.info("Order captured: {}", token);
    return orderService.captureOrder(token, payerID)
        .then(Mono.defer(() -> {
          String redirectUrl = frontendUrl + "?paymentStatus=" + PaymentStatus.PAID;
          response.setStatusCode(HttpStatus.SEE_OTHER);
          response.getHeaders().setLocation(URI.create(redirectUrl));
          return response.setComplete();
        }))
        .onErrorResume(ex -> {
          log.error("Failed to capture order for token {}: {}", token, ex.getMessage());
          String redirectUrl = frontendUrl + "?paymentStatus=FAILED";
          response.setStatusCode(HttpStatus.SEE_OTHER);
          response.getHeaders().setLocation(URI.create(redirectUrl));
          return response.setComplete();
        });
  }

  @GetMapping("/cancel")
  public Mono<Void> cancelPayPalOrder(
      @RequestParam("token") String token,
      ServerHttpResponse response
  ) {
    log.info("Order cancelled: {}", token);
    return orderService.cancelOrder(token)
        .then(Mono.defer(() -> {
          String redirectUrl = frontendUrl + "?paymentStatus=" + PaymentStatus.CANCELLED;
          response.setStatusCode(HttpStatus.SEE_OTHER);
          response.getHeaders().setLocation(URI.create(redirectUrl));
          return response.setComplete();
        }))
        .onErrorResume(ex -> {
          log.error("Failed to cancel order for token {}: {}", token, ex.getMessage());
          String redirectUrl = frontendUrl + "?paymentStatus=FAILED";
          response.setStatusCode(HttpStatus.SEE_OTHER);
          response.getHeaders().setLocation(URI.create(redirectUrl));
          return response.setComplete();
        });
  }
}
