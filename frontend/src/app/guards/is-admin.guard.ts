import { inject } from '@angular/core';
import { type CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { take, map } from 'rxjs';
import { UserRole } from '../interfaces/model/User';

export const isAdminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.user$.pipe(
    take(1),
    map((user) =>
      !user || user.role !== UserRole.ADMIN
        ? router.createUrlTree(['/home'])
        : true,
    ),
  );
};
