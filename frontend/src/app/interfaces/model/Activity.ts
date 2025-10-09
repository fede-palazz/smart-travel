import { Coordinates } from './shared/Coordinates';
import { DestinationSummary } from './shared/DestinationSummary';
import { Price } from './shared/Price';
import { ReviewsSummary } from './shared/ReviewsSummary';
import { Schedule } from './shared/Schedule';

export interface Activity {
  id: string;
  name: string;
  type: string;
  description: string;
  notes: string;
  address: string;
  coordinates: Coordinates;
  destination: DestinationSummary;
  mainPicture: string;
  pictures: string[];
  tags: string[];
  languages: string[];
  schedule: Schedule;
  price: Price;
  reviewsSummary: ReviewsSummary;
}
