import { Coordinates } from './Coordinates';
import { Country } from './Country';

export interface FlightDestination {
  destinationId: string;
  city: string;
  region: string;
  country: Country;
  coordinates: Coordinates;
  timezone: string;
  airportCode: string;
  airportName: string;
}
