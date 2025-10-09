import { Coordinates } from './Coordinates';
import { Country } from './Country';

export interface DestinationSummary {
  destinationId: string;
  city: string;
  region: string;
  country: Country;
  coordinates: Coordinates;
  timezone: string;
}
