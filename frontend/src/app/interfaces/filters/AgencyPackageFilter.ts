export interface AgencyPackageFilter {
  name?: string;
  tags?: string[];
  status?: string;
  minPrice?: number;
  maxPrice?: number;
  authorId?: string;
}

export interface FullAgencyPackageFilter extends AgencyPackageFilter {
  to?: string;
  toType?: string;
  startDate?: string;
  endDate?: string;
}
