import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { TooltipModule } from 'primeng/tooltip';
import { FlightDetailsComponent } from '../../../flight/ui/flight-details/flight-details.component';
import { FlightOrder } from '../../../../interfaces/orders/FlightOrder';
import { DateUtils } from '../../../../utils/DateUtils';
import { Flight } from '../../../../interfaces/model/Flight';

@Component({
  selector: 'smt-agency-package-details-flight',
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
  templateUrl: './agency-package-details-flight.component.html',
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
export class AgencyPackageDetailsFlightComponent {
  // Local variables
  dateUtils = new DateUtils();
  flightDetails!: Flight;

  // State variables
  @Input({ required: true }) flight!: FlightOrder;

  // Events
  @Output() onSelectFlight = new EventEmitter<FlightOrder>();

  ngOnInit(): void {
    this.flightDetails = {
      ...this.flight,
      id: this.flight.flightId,
      capacity: 0,
    };
  }

  handleSelectFlight(event$: Event, flight: FlightOrder) {
    event$.stopPropagation();
    this.onSelectFlight.emit(flight);
  }
}
