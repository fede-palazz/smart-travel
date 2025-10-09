import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { CheckoutService } from './services/checkout.service';
import { Observable } from 'rxjs';
import { OrderItems } from '../../interfaces/orders/Order';
import { CheckoutConfirmationComponent } from './ui/checkout-confirmation/checkout-confirmation.component';
import { Router } from '@angular/router';
import {
  PartialAccommodationOrder,
  PartialActivityOrder,
  PartialFlightOrder,
} from '../../interfaces/orders/OrderReq';
import { PackageStep } from '../../interfaces/enums/PackageStep';

@Component({
  selector: 'smt-checkout',
  standalone: true,
  imports: [CommonModule, CheckoutConfirmationComponent],
  templateUrl: './checkout.component.html',
  styles: ``,
})
export class CheckoutComponent implements OnInit {
  // State variables
  order$!: Observable<OrderItems>;

  // Injectables
  private router = inject(Router);
  private checkoutService = inject(CheckoutService);

  ngOnInit() {
    this.order$ = this.checkoutService.order$;
  }

  handleEditDepartureFlight() {
    const returnUrl = this.checkoutService.getReturnUrl();
    const updatedUrl = returnUrl.includes('?')
      ? `${returnUrl}&step=${PackageStep.FLIGHT_DEPARTURE}`
      : `${returnUrl}?step=${PackageStep.FLIGHT_DEPARTURE}`;
    this.router.navigateByUrl(updatedUrl);
  }

  handleEditReturnFlight() {
    const returnUrl = this.checkoutService.getReturnUrl();
    const updatedUrl = returnUrl.includes('?')
      ? `${returnUrl}&step=${PackageStep.FLIGHT_RETURN}`
      : `${returnUrl}?step=${PackageStep.FLIGHT_RETURN}`;

    this.router.navigateByUrl(updatedUrl);
  }

  handleEditAccommodation() {
    const returnUrl = this.checkoutService.getReturnUrl();
    const updatedUrl = returnUrl.includes('?')
      ? `${returnUrl}&step=${PackageStep.ACCOMMODATION}`
      : `${returnUrl}?step=${PackageStep.ACCOMMODATION}`;
    this.router.navigateByUrl(updatedUrl);
  }

  handleAddActivity() {
    const returnUrl = this.checkoutService.getReturnUrl();
    const updatedUrl = returnUrl.includes('?')
      ? `${returnUrl}&step=${PackageStep.ACTIVITY}`
      : `${returnUrl}?step=${PackageStep.ACTIVITY}`;
    this.router.navigateByUrl(updatedUrl);
  }

  handleDeleteActivity(id: string) {
    this.checkoutService.removeActivity(id);
    // Check for empty order
    if (this.checkoutService.isEmpty()) this.router.navigate(['/home']);
  }

  handleCheckout() {
    const { departureFlight, returnFlight, accommodation, activities } =
      this.checkoutService.getOrder();

    const partialDepartureFlight: PartialFlightOrder | undefined =
      departureFlight
        ? {
            flightId: departureFlight.flightId,
            quantity: departureFlight.quantity,
          }
        : undefined;

    const partialReturnFlight: PartialFlightOrder | undefined = returnFlight
      ? {
          flightId: returnFlight.flightId,
          quantity: returnFlight.quantity,
        }
      : undefined;
    const partialAccommodation: PartialAccommodationOrder | undefined =
      accommodation
        ? {
            accommodationId: accommodation.accommodationId,
            startDate: accommodation.startDate,
            endDate: accommodation.endDate,
            rooms: accommodation.rooms,
          }
        : undefined;

    const partialActivities: PartialActivityOrder[] | undefined =
      activities && activities.length > 0
        ? activities.map((activity) => ({
            activityId: activity.activityId,
            date: activity.date,
            quantity: activity.quantity,
          }))
        : undefined;

    this.checkoutService
      .performCheckout(
        partialDepartureFlight,
        partialReturnFlight,
        partialAccommodation,
        partialActivities,
      )
      .subscribe(({ data, error }) => {
        if (error) {
          console.error(error);
          return;
        }
        if (data) window.location.href = data.redirectURL;
      });
  }
}
