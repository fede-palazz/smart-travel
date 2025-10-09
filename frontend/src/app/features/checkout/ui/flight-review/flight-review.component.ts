import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Flight } from '../../../../interfaces/model/Flight';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { CommonModule } from '@angular/common';
import { TooltipModule } from 'primeng/tooltip';
import { DividerModule } from 'primeng/divider';
import { ButtonModule } from 'primeng/button';
import { DateUtils } from '../../../../utils/DateUtils';
import { FlightDetailsComponent } from '../../../flight/ui/flight-details/flight-details.component';

@Component({
  selector: 'smt-flight-review',
  standalone: true,
  imports: [
    AccordionModule,
    AvatarModule,
    CommonModule,
    TooltipModule,
    DividerModule,
    ButtonModule,
    FlightDetailsComponent,
  ],
  templateUrl: './flight-review.component.html',
  styles: `
    ::ng-deep
      .flight-review
      .p-accordion
      .p-accordion-header
      .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
    ::ng-deep
      .flight-review
      .p-accordion
      .p-accordion-header
      .p-accordion-header-link {
      padding: 0.5rem;
    }
  `,
})
export class FlightReviewComponent {
  // Local variables
  dateUtils = new DateUtils();

  // State variables
  @Input() hideAction: boolean = false;
  @Input({ required: true }) flight!: Flight;

  // Events
  @Output() onEditFlight = new EventEmitter<Flight>();

  handleEditFlight(event$: Event, flight: Flight) {
    event$.stopPropagation();
    this.onEditFlight.emit(flight);
  }
}
