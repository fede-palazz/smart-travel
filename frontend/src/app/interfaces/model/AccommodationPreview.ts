import { Coordinates } from './shared/Coordinates';
import { DestinationSummary } from './shared/DestinationSummary';
import { ReviewsSummary } from './shared/ReviewsSummary';

export interface AccommodationPreview {
  id: string;
  name: string;
  type: string;
  services: string[];
  destination: DestinationSummary;
  address: string;
  coordinates: Coordinates;
  distanceToCenterKm: number;
  pricePerNight: number;
  mainPicture: string;
  reviewsSummary: ReviewsSummary;
}
