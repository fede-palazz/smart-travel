package com.certimetergroup.smart_travel.ordersapi.dto.response;

import com.certimetergroup.smart_travel.ordersapi.model.Payment;
import java.time.Instant;
import shared.UserSummary;
import shared.order.OrderItems;
import shared.order.OrderType;

public record OrderResDTO(
    String id,
    UserSummary customerInfo,
    Instant createdAt,
    OrderType type,
    OrderItems items,
    Payment payment,
    String agencyPackageId // In case of agency package

) {

}
