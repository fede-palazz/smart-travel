package shared.order;

public enum PaymentStatus {
  PENDING,    // Customer placed order, but it has not been paid yet
  PAID,       // Customer successfully paid the order, amount has not been collected yet
  EXPIRED,    // Customer did not pay the order within the allowed time frame
  CANCELLED,  // Customer cancelled the order before it was set as expired
  CONFIRMED,  // Customer successfully paid and reserved a vehicle
}
