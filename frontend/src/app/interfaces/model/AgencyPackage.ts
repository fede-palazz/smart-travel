import { PackageStatus } from '../enums/PackageStatus';
import { AccommodationOrder } from '../orders/AccommodationOrder';
import { ActivityOrder } from '../orders/ActivityOrder';
import { FlightOrder } from '../orders/FlightOrder';
import { DestinationSummary } from './shared/DestinationSummary';
import { Price } from './shared/Price';
import { UserSummary } from './shared/UserSummary';

export interface AgencyPackage {
  id: string;
  name: string;
  description: string;
  tags: string[];
  status: PackageStatus;
  startDate: string;
  endDate: string;
  totalPrice: Price;
  destination: DestinationSummary;
  mainPicture: string;
  pictures?: string[];
  departureFlight: FlightOrder;
  returnFlight: FlightOrder;
  accommodation: AccommodationOrder;
  activities: ActivityOrder[];
  agentInfo: UserSummary;
  creationDate: string;
  quantity: number;
}

export interface PartialAgencyPackage {
  name: string;
  description: string;
  tags: string[];
  totalPrice: Price;
  mainPicture: string;
}
