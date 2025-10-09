export interface AccommodationFilter {
  name?: string;
  types?: string[];
  services?: string[];
  minDistanceToCenterKm?: number;
  maxDistanceToCenterKm?: number;
  minPricePerNight?: number;
  maxPricePerNight?: number;
  minRating?: number;
}
