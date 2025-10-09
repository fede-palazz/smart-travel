import { FlightDestination } from './FlightDestination';

export interface FlightSummary {
  flightId: string;
  code: string;
  airline: string;
  airlineLogo: string;
  from: FlightDestination;
  to: FlightDestination;
}
