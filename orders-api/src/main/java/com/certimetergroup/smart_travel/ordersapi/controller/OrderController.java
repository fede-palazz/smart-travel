package com.certimetergroup.smart_travel.ordersapi.controller;


import com.certimetergroup.smart_travel.ordersapi.dto.request.CancelOrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.request.CaptureOrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.request.OrderReqDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.OrderResDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.PagedResDTO;
import com.certimetergroup.smart_travel.ordersapi.dto.response.PaymentUrlResDTO;
import com.certimetergroup.smart_travel.ordersapi.exception.FailureException;
import com.certimetergroup.smart_travel.ordersapi.exception.ResponseEnum;
import com.certimetergroup.smart_travel.ordersapi.filter.OrderFilter;
import com.certimetergroup.smart_travel.ordersapi.service.OrderService;
import com.certimetergroup.smart_travel.ordersapi.validator.ControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @Operation(
      summary = "Get all orders",
      responses = @ApiResponse(responseCode = "200"),
      parameters = {
          @Parameter(name = "page", description = "Page number", example = "0"),
          @Parameter(name = "size", description = "Page size", example = "10"),
          @Parameter(name = "sort", description = "Field to sort by", example = "createdAt"),
          @Parameter(name = "order", description = "Sort direction (asc or desc)", example = "asc"),

          @Parameter(name = "customerId", description = "Filter by customer ID", example = "abc123"),
          @Parameter(name = "customerName", description = "Search by customer first or last name", example = "John"),
          @Parameter(name = "minAmount", description = "Minimum order amount", example = "100.0"),
          @Parameter(name = "maxAmount", description = "Maximum order amount", example = "1000.0"),
          @Parameter(name = "status", description = "Payment status (PENDING, PAID, CANCELLED, CONFIRMED)", example = "PAID")
      }
  )
  @GetMapping("")
  public Mono<ResponseEntity<PagedResDTO<OrderResDTO>>> getOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sort,
      @RequestParam(defaultValue = "desc") String order,
      @ModelAttribute OrderFilter filters
  ) {
    var allowedSortValues = List.of("createdAt", "type");
    // Validate query params
    ControllerValidator.validate(size, page, sort, order, allowedSortValues);

    return orderService.getOrders(page, size, sort, order, filters).map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Get order by id",
      parameters = @Parameter(
          name = "id",
          description = "ID of the order to fetch",
          required = true,
          in = ParameterIn.PATH
      ),
      responses = {
          @ApiResponse(responseCode = "200"),
          @ApiResponse(responseCode = "400"),
          @ApiResponse(responseCode = "404", content = @Content())
      }
  )
  @GetMapping("{id}")
  public Mono<ResponseEntity<OrderResDTO>> getOrderById(@PathVariable String id) {
    // Check that id is valid
    try {
      ObjectId objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_INPUT, "Invalid id format");
    }
    return orderService.getOrderById(id).map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Create a new order",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = OrderReqDTO.class)
          )}
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = {@Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrderResDTO.class)
              )}
          ),
          @ApiResponse(responseCode = "400")
      }
  )
  @PostMapping("")
  public Mono<ResponseEntity<PaymentUrlResDTO>> createOrder(@RequestBody OrderReqDTO orderReqDTO) {
    return this.orderService.createOrder(orderReqDTO)
        .map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Delete order by id",
      parameters = @Parameter(
          name = "id",
          description = "ID of the user to be deleted",
          required = true,
          in = ParameterIn.PATH
      ),
      responses = {
          @ApiResponse(responseCode = "204"),
          @ApiResponse(responseCode = "400"),
          @ApiResponse(responseCode = "404", content = @Content())
      }
  )
  @DeleteMapping("{id}")
  public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable String id) {
    // Check that id is valid
    try {
      ObjectId objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      return Mono.error(new FailureException(ResponseEnum.INVALID_INPUT, "Invalid id format"));
    }
    return orderService.deleteOrder(id)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }

  @PostMapping("capture")
  public Mono<ResponseEntity<Void>> captureOrder(@RequestBody CaptureOrderReqDTO captureReq) {
    return orderService.captureOrder(captureReq)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }

  @PostMapping("cancel")
  public Mono<ResponseEntity<Void>> cancelOrder(@RequestBody CancelOrderReqDTO cancelReq) {
    return orderService.cancelOrder(cancelReq)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }
}
