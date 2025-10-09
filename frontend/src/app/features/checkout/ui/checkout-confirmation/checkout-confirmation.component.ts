import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { TextComponent } from '../../../../shared/text.component';
import { DateUtils } from '../../../../utils/DateUtils';
import { FlightOrder } from '../../../../interfaces/orders/FlightOrder';
import { Flight } from '../../../../interfaces/model/Flight';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ConfirmationService } from 'primeng/api';
import { CheckoutService } from '../../services/checkout.service';
import { Router } from '@angular/router';
import { AccommodationOrder } from '../../../../interfaces/orders/AccommodationOrder';
import { AccommodationReviewComponent } from '../accommodation-review/accommodation-review.component';
import { ActivityOrder } from '../../../../interfaces/orders/ActivityOrder';
import { ActivityReviewComponent } from '../activity-review/activity-review.component';
import { FlightReviewComponent } from '../flight-review/flight-review.component';

@Component({
  selector: 'smt-checkout-confirmation',
  standalone: true,
  imports: [
    TextComponent,
    CardModule,
    ButtonModule,
    FlightReviewComponent,
    DividerModule,
    CommonModule,
    ConfirmPopupModule,
    AccommodationReviewComponent,
    ActivityReviewComponent,
  ],
  templateUrl: './checkout-confirmation.component.html',
  styles: `
    :host ::ng-deep .checkout-container .p-card .p-card-body {
      padding-inline: 2rem;
    }
  `,
  providers: [ConfirmationService],
})
export class CheckoutConfirmationComponent {
  // Local variables
  dateUtils = new DateUtils();
  isCheckingOut = false; // loading spinner

  // State variables
  @Input() departureFlight?: FlightOrder;
  @Input() returnFlight?: FlightOrder;
  @Input() accommodation?: AccommodationOrder;
  @Input() activities?: ActivityOrder[];
  @Input() isAgentView: boolean = false;

  // Injectables
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);
  private checkoutService = inject(CheckoutService);

  // Events
  @Output() onEditDepartureFlight = new EventEmitter();
  @Output() onEditReturnFlight = new EventEmitter();
  @Output() onEditAccommodation = new EventEmitter();
  @Output() onAddActivity = new EventEmitter();
  @Output() onDeleteActivity = new EventEmitter<string>();
  @Output() onCheckout = new EventEmitter();

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

  isOrderEmpty(): boolean {
    return this.checkoutService.isEmpty();
  }

  handleEditFlight(flight: Flight) {
    if (flight.id === this.departureFlight?.flightId) {
      // Edit departure flight
      this.onEditDepartureFlight.emit();
    } else {
      // Edit return flight
      this.onEditReturnFlight.emit();
    }
  }

  handleEditAccommodation() {
    this.onEditAccommodation.emit();
  }

  handleAddActivity() {
    this.onAddActivity.emit();
  }

  handleDeleteActivity(activityId: string) {
    this.onDeleteActivity.emit(activityId);
  }

  handleCancelOrder(event: Event) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure you want to proceed?',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.checkoutService.clearOrder();
        this.router.navigate(['/home']);
      },
      reject: () => {},
    });
  }

  handleCheckout() {
    this.isCheckingOut = true; // show loading spinner
    this.onCheckout.emit();
  }
}
