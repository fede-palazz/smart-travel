import { Component, Input } from '@angular/core';
import { AccommodationOrder } from '../../../../../interfaces/orders/AccommodationOrder';
import { ActivityOrder } from '../../../../../interfaces/orders/ActivityOrder';
import { FlightOrder } from '../../../../../interfaces/orders/FlightOrder';
import { DateUtils } from '../../../../../utils/DateUtils';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { TextComponent } from '../../../../../shared/text.component';
import { AccommodationReviewComponent } from '../../../../checkout/ui/accommodation-review/accommodation-review.component';
import { ActivityReviewComponent } from '../../../../checkout/ui/activity-review/activity-review.component';
import { FlightReviewComponent } from '../../../../checkout/ui/flight-review/flight-review.component';
import { Order } from '../../../../../interfaces/orders/Order';

@Component({
  selector: 'smt-orders-details-items',
  standalone: true,
  imports: [
    TextComponent,
    CardModule,
    ButtonModule,
    FlightReviewComponent,
    DividerModule,
    CommonModule,
    AccommodationReviewComponent,
    ActivityReviewComponent,
  ],
  templateUrl: './orders-details-items.component.html',
  styles: ``,
})
export class OrdersDetailsItemsComponent {
  // Local variables
  dateUtils = new DateUtils();
  departureFlight?: FlightOrder;
  returnFlight?: FlightOrder;
  accommodation?: AccommodationOrder;
  activities?: ActivityOrder[];

  // State variables
  @Input({ required: true }) order!: Order;
  @Input() isAgentView: boolean = false;

  ngOnInit(): void {
    this.departureFlight = this.order.items.departureFlight;
    this.returnFlight = this.order.items.returnFlight;
    this.accommodation = this.order.items.accommodation;
    this.activities = this.order.items.activities;
  }

  getTotalPrice() {
    let totalPrice = 0;

    if (this.departureFlight) {
      totalPrice +=
        this.departureFlight.price.value * this.departureFlight.quantity;
    }
    if (this.returnFlight) {
      totalPrice += this.returnFlight.price.value * this.returnFlight.quantity;
    }
    if (this.accommodation) {
      const numOfNights = this.getAccommodationNightNumber();
      totalPrice += this.getRoomsTotalPrice() * numOfNights;
    }
    if (this.activities) {
      totalPrice += this.getActivitiesTotalPrice();
    }
    return totalPrice;
  }

  getRoomsTotalPrice(): number {
    return (
      this.accommodation?.rooms.reduce(
        (acc, room) => acc + room.pricePerNight.value * room.quantity,
        0,
      ) ?? 0
    );
  }

  getActivitiesTotalPrice(): number {
    return (
      this.activities?.reduce(
        (acc, activity) => acc + activity.price.value * activity.quantity,
        0,
      ) ?? 0
    );
  }

  getAccommodationNightNumber(): number {
    const start = new Date(this.accommodation!.startDate);
    const end = new Date(this.accommodation!.endDate);

    // Ensure dates are valid
    if (isNaN(start.getTime()) || isNaN(end.getTime())) return 0;

    const diffInMs = end.getTime() - start.getTime();
    const diffInDays = diffInMs / (1000 * 60 * 60 * 24);

    return Math.max(0, Math.floor(diffInDays));
  }
}
