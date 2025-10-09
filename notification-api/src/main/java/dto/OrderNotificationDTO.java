package dto;

import shared.Price;
import shared.UserSummary;
import shared.order.OrderItems;

public record OrderNotificationDTO(
    String id,
    UserSummary customerInfo,
    OrderItems items,
    Price amount,
    String agencyPackageId // In case of agency package
) {

}