import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { AccommodationOrder } from '../../../../interfaces/orders/AccommodationOrder';
import { AccommodationDetailsRoomsComponent } from '../../../accommodation-details/ui/accommodation-details-rooms/accommodation-details-rooms.component';

@Component({
  selector: 'smt-agency-package-details-accommodation',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    ButtonModule,
    AvatarModule,
    AccommodationDetailsRoomsComponent,
  ],
  templateUrl: './agency-package-details-accommodation.component.html',
  styles: `
    ::ng-deep
      .accommodation-review
      .p-accordion
      .p-accordion-header
      .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
    ::ng-deep
      .accommodation-review
      .p-accordion
      .p-accordion-header
      .p-accordion-header-link {
      padding: 0.5rem;
    }
  `,
})
export class AgencyPackageDetailsAccommodationComponent {
  @Input({ alias: 'accommodation', required: true })
  accommodationOrder!: AccommodationOrder;
  @Output() onViewAccommodation = new EventEmitter<AccommodationOrder>();

  handleViewAccommodation(event: Event) {
    event.stopPropagation();
    this.onViewAccommodation.emit(this.accommodationOrder);
  }
}
