import { Coordinates } from './shared/Coordinates';
import { Country } from './shared/Country';

export interface Destination {
  id: string;
  city: string;
  region: string;
  country: Country;
  coordinates: Coordinates;
  description: string;
  pictures: string[];
  tags: string[];
  popularityScore: number;
  timezone: String;
}

export type DestinationPreview = Pick<
  Destination,
  'id' | 'city' | 'description' | 'country' | 'pictures'
>;
