import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { DataViewModule } from 'primeng/dataview';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { RatingModule } from 'primeng/rating';
import { TagModule } from 'primeng/tag';
import { AgencyPackagePreview } from '../../../../interfaces/model/AgencyPackagePreview';

@Component({
  selector: 'smt-agency-package-list',
  standalone: true,
  imports: [
    DataViewModule,
    TagModule,
    RatingModule,
    ButtonModule,
    CommonModule,
    PaginatorModule,
    TagModule,
  ],
  templateUrl: './agency-package-list.component.html',
  styles: ``,
})
export class AgencyPackageListComponent {
  // State variables
  @Input() pageSize: number = 5;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;
  @Input({ required: true }) packages!: AgencyPackagePreview[];

  // Events
  @Output() onSelectAgencyPackage = new EventEmitter<AgencyPackagePreview>();
  @Output() onPageChange = new EventEmitter<number>();

  handleSelectAgencyPackage(agencyPackage: AgencyPackagePreview) {
    this.onSelectAgencyPackage.emit(agencyPackage);
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }
}
