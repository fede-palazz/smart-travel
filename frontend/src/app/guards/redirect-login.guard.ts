import { inject } from '@angular/core';
import { type CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const redirectLoginGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Redirect to /login with returnUrl
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url },
  });
};
