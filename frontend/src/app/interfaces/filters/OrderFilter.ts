export interface OrderFilter {
  orderId?: string;
  customerId?: string;
  customerName?: string; // Either firstname or surname or both
  minAmount?: number;
  maxAmount?: number;
  status?: string;
  type?: string;
}
