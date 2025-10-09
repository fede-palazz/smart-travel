import { Price } from './Price';

export interface Room {
  name: string;
  type: string;
  capacity: number;
  pricePerNight: Price;
  quantity: number;
  amenities?: string[];
  bedTypes?: string[];
  pictures?: string[];
}
