import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccommodationPreview } from '../../../../interfaces/model/AccommodationPreview';
import { DataViewModule } from 'primeng/dataview';
import { TagModule } from 'primeng/tag';
import { RatingModule } from 'primeng/rating';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';

@Component({
  selector: 'smt-accommodation-list',
  standalone: true,
  imports: [
    DataViewModule,
    TagModule,
    RatingModule,
    ButtonModule,
    CommonModule,
    PaginatorModule,
  ],
  templateUrl: './accommodation-list.component.html',
  styles: ``,
})
export class AccommodationListComponent {
  // State variables
  @Input() pageSize: number = 5;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;
  @Input({ required: true }) accommodations!: AccommodationPreview[];

  // Events
  @Output() onSelectAccommodation = new EventEmitter<AccommodationPreview>();
  @Output() onPageChange = new EventEmitter<number>();

  handleSelectAccommodation(accommodation: AccommodationPreview) {
    this.onSelectAccommodation.emit(accommodation);
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }
}
