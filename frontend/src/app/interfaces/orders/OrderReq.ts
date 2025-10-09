import { Room } from '../model/shared/Room';

export interface PartialOrderReq {
  departureFlight?: PartialFlightOrder;
  returnFlight?: PartialFlightOrder;
  accommodation?: PartialAccommodationOrder;
  activities?: PartialActivityOrder[];
}

export interface PartialAgencyOrderReq {
  agencyPackageId: string;
}

export interface PartialFlightOrder {
  flightId: string;
  quantity: number;
}

export interface PartialAccommodationOrder {
  accommodationId: string;
  rooms: Room[];
  startDate: string;
  endDate: string;
}

export interface PartialActivityOrder {
  activityId: string;
  date: string;
  quantity: number;
}
