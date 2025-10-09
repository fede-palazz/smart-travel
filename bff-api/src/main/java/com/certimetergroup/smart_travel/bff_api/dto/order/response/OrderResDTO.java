package com.certimetergroup.smart_travel.bff_api.dto.order.response;

import java.time.Instant;
import shared.UserSummary;
import shared.order.OrderItems;
import shared.order.OrderType;
import shared.order.Payment;

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
