import { Room } from '../model/shared/Room';

export interface AccommodationOrder {
  accommodationId: string;
  name: string;
  type: string;
  mainPicture: string;
  rooms: Room[];
  startDate: string;
  endDate: string;
}
