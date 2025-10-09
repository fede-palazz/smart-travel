import { inject } from '@angular/core';
import { type CanActivateFn, Router } from '@angular/router';
import { CheckoutService } from '../features/checkout/services/checkout.service';

export const IsOrderPresentGuard: CanActivateFn = (route, state) => {
  const checkoutService = inject(CheckoutService);
  const router = inject(Router);

  // If no order has been submitted, stop the navigation
  return !checkoutService.isOrderPresent()
    ? router.createUrlTree(['/home'])
    : true;
};
