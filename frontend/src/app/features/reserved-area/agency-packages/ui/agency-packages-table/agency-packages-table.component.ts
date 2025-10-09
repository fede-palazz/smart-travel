import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TableModule, TablePageEvent } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { AgencyPackagePreview } from '../../../../../interfaces/model/AgencyPackagePreview';
import { PackageStatus } from '../../../../../interfaces/enums/PackageStatus';
import { MenuModule } from 'primeng/menu';
import { RippleModule } from 'primeng/ripple';
import { ButtonModule } from 'primeng/button';
import { AgencyPackagesActionsComponent } from '../agency-packages-actions/agency-packages-actions.component';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';

@Component({
  selector: 'smt-agency-packages-table',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    MenuModule,
    RippleModule,
    ButtonModule,
    PaginatorModule,
    AgencyPackagesActionsComponent,
  ],
  templateUrl: './agency-packages-table.component.html',
  styles: `
    ::ng-deep .p-datatable-footer {
      padding: 0;
    }
  `,
})
export class AgencyPackagesTableComponent {
  // Status variables
  @Input({ required: true }) packages!: AgencyPackagePreview[];
  @Input({ required: true }) pageSize!: number;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;

  // Events
  @Output() onViewDetails = new EventEmitter<string>();
  @Output() onPublishPackage = new EventEmitter<string>();
  @Output() onArchivePackage = new EventEmitter<string>();
  @Output() onDeletePackage = new EventEmitter<string>();
  @Output() onPageChange = new EventEmitter<number>();

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

  handleViewDetails(id: string) {
    this.onViewDetails.emit(id);
  }

  handlePublishPackage(id: string) {
    this.onPublishPackage.emit(id);
  }

  handleArchivePackage(id: string) {
    this.onArchivePackage.emit(id);
  }

  handleDeletePackage(id: string) {
    this.onDeletePackage.emit(id);
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }
}
