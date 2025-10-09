import { Price } from '../model/shared/Price';

export interface ActivityOrder {
  activityId: string;
  name: string;
  type: string;
  mainPicture: string;
  date: string;
  price: Price;
  startTime: string;
  endTime: string;
  quantity: number;
}
