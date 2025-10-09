import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { take, map } from 'rxjs';
import { UserRole } from '../interfaces/model/User';
import { AuthService } from '../services/auth.service';

export const isUnauthenticatedOrCustomerGuard: CanActivateFn = (
  route,
  state,
) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.user$.pipe(
    take(1),
    map(
      (user) =>
        !user || user.role === UserRole.CUSTOMER
          ? true
          : router.createUrlTree(['/dashboard/agency-packages']), // redirect agent/admin
    ),
  );
};
