import { Contacts } from './shared/Contacts';
import { Coordinates } from './shared/Coordinates';
import { DestinationSummary } from './shared/DestinationSummary';
import { Policies } from './shared/Policies';
import { ReviewsSummary } from './shared/ReviewsSummary';
import { Room } from './shared/Room';

export interface Accommodation {
  id: string;
  name: string;
  type: string;
  address: string;
  coordinates: Coordinates;
  destination: DestinationSummary;
  distanceToCenterKm: number;
  mainPicture: string;
  description: string;
  details: string;
  checkInTime: string;
  checkOutTime: string;
  contacts: Contacts;
  policies: Policies;
  pictures: string[];
  services: string[];
  languages: string[];
  rooms: Room[];
  reviewsSummary: ReviewsSummary;
}
