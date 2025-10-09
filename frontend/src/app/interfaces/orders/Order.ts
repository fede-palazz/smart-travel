import { Price } from '../model/shared/Price';
import { UserSummary } from '../model/shared/UserSummary';
import { AccommodationOrder } from './AccommodationOrder';
import { ActivityOrder } from './ActivityOrder';
import { FlightOrder } from './FlightOrder';

export interface Order {
  id: string;
  customerInfo: UserSummary;
  createdAt: string;
  type: OrderType;
  items: OrderItems;
  payment: Payment;
  agencyPackageId: string;
}

export interface OrderPreview {
  id: string;
  customerInfo: UserSummary;
  createdAt: string;
  type: OrderType;
  payment: Payment;
  agencyPackageId: string;
}

export enum OrderType {
  SINGLE = 'SINGLE', // Single product (flight, accommodation, activity)
  AGENCY = 'AGENCY', // Agency package
  CUSTOM = 'CUSTOM', // Custom package
}

export interface OrderItems {
  departureFlight?: FlightOrder;
  returnFlight?: FlightOrder;
  accommodation?: AccommodationOrder;
  activities?: ActivityOrder[];
}

export interface Payment {
  status: PaymentStatus;
  paidAt: string;
  transactionId: string;
  amount: Price;
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
  CONFIRMED = 'CONFIRMED',
}
