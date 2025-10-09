import { DestinationSummary } from '../model/shared/DestinationSummary';
import { Price } from '../model/shared/Price';
import { UserSummary } from '../model/shared/UserSummary';
import { AccommodationOrder } from './AccommodationOrder';
import { ActivityOrder } from './ActivityOrder';
import { FlightOrder } from './FlightOrder';

export interface AgencyPackageReq {
  name: string;
  description: string;
  tags: string[];
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
  quantity: number;
}

export interface PartialAgencyPackageReq {
  name: string;
  description: string;
  tags: string[];
  totalPrice: Price;
  mainPicture: string;
}
