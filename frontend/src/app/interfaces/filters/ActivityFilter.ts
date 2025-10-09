export interface ActivityFilter {
  name?: string;
  types?: string[];
  tags?: string[];
  languages?: string[];
  minPrice?: number;
  maxPrice?: number;
  minRating?: number;
}
