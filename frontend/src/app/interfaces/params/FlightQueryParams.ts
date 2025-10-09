export interface FlightQueryParams {
  type: string;
  from: string;
  fromType: string;
  to: string;
  toType: string;
  startDate: string;
  endDate?: string;
  quantity: number;
}
