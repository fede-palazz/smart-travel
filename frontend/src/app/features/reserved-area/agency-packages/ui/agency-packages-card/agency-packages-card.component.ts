import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AgencyPackagePreview } from '../../../../../interfaces/model/AgencyPackagePreview';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { PackageStatus } from '../../../../../interfaces/enums/PackageStatus';

@Component({
  selector: 'smt-agency-packages-card',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule, TooltipModule, TagModule],
  templateUrl: './agency-packages-card.component.html',
  styles: `
    ::ng-deep .p-card-content {
      padding: 0px;
    }
  `,
})
export class AgencyPackagesCardComponent {
  // Status variables
  @Input({ required: true }) package!: AgencyPackagePreview;

  // Events
  @Output() onViewDetails = new EventEmitter<string>();

  getSeverity(agencyPackage: AgencyPackagePreview) {
    switch (agencyPackage.status) {
      case PackageStatus.PUBLISHED:
        return 'success';

      case PackageStatus.ARCHIVED:
        return 'warning';

      case PackageStatus.DRAFT:
        return 'danger';

      default:
        return undefined;
    }
  }

  handleViewDetails() {
    this.onViewDetails.emit(this.package.id);
  }
}
