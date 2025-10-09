import { PackageStatus } from '../enums/PackageStatus';
import { DestinationSummary } from './shared/DestinationSummary';
import { Price } from './shared/Price';
import { UserSummary } from './shared/UserSummary';

export interface AgencyPackagePreview {
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
  agentInfo: UserSummary;
  creationDate: string;
  quantity: number;
}
