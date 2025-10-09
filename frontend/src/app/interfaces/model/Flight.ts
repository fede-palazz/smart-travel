import { FlightDestination } from './shared/FlightDestination';
import { Price } from './shared/Price';

export interface Flight {
  id: string;
  code: string;
  capacity: number;
  airline: string;
  airlineLogo: string;
  from: FlightDestination;
  to: FlightDestination;
  departureTime: string;
  arrivalTime: string;
  price: Price;
}
