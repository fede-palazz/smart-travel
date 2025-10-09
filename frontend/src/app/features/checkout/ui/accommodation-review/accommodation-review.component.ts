import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { ButtonModule } from 'primeng/button';
import { AccommodationOrder } from '../../../../interfaces/orders/AccommodationOrder';
import { AvatarModule } from 'primeng/avatar';
import { AccommodationDetailsRoomsComponent } from '../../../accommodation-details/ui/accommodation-details-rooms/accommodation-details-rooms.component';

@Component({
  selector: 'smt-accommodation-review',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    ButtonModule,
    AvatarModule,
    AccommodationDetailsRoomsComponent,
  ],
  templateUrl: './accommodation-review.component.html',
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
export class AccommodationReviewComponent {
  @Input() hideAction: boolean = false;
  @Input({ alias: 'accommodation', required: true })
  accommodationOrder!: AccommodationOrder;
  @Output() onEditAccommodation = new EventEmitter();

  getRoomsTotalPrice(): number {
    return this.accommodationOrder.rooms.reduce(
      (acc, room) => acc + room.pricePerNight.value * room.quantity,
      0,
    );
  }

  handleEditAccommodation() {
    this.onEditAccommodation.emit();
  }
}
