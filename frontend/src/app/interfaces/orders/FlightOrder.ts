import { FlightDestination } from '../model/shared/FlightDestination';
import { Price } from '../model/shared/Price';

export interface FlightOrder {
  flightId: string;
  code: string;
  quantity: number;
  airline: string;
  airlineLogo: string;
  from: FlightDestination;
  to: FlightDestination;
  departureTime: string;
  arrivalTime: string;
  price: Price;
}
