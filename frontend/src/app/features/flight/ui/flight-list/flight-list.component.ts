import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Flight } from '../../../../interfaces/model/Flight';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { CommonModule } from '@angular/common';
import { TooltipModule } from 'primeng/tooltip';
import { DividerModule } from 'primeng/divider';
import { ButtonModule } from 'primeng/button';
import { FlightDetailsComponent } from '../flight-details/flight-details.component';
import { DateUtils } from '../../../../utils/DateUtils';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';

@Component({
  selector: 'smt-flight-list',
  standalone: true,
  imports: [
    AccordionModule,
    AvatarModule,
    CommonModule,
    TooltipModule,
    DividerModule,
    ButtonModule,
    FlightDetailsComponent,
    PaginatorModule,
  ],
  templateUrl: './flight-list.component.html',
  styles: `
    ::ng-deep
      .flight-list
      .p-accordion
      .p-accordion-header
      .p-accordion-header-link {
      padding: 0.5rem;
    }
  `,
})
export class FlightListComponent {
  // Local variables
  dateUtils = new DateUtils();

  // State variables
  @Input({ required: true }) flights: Flight[] = [];
  @Input() pageSize: number = 5;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;

  // Events
  @Output() onSelectFlight = new EventEmitter<Flight>();
  @Output() onPageChange = new EventEmitter<number>();

  handleSelectFlight(event$: Event, flight: Flight) {
    event$.stopPropagation();
    this.onSelectFlight.emit(flight);
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }
}
