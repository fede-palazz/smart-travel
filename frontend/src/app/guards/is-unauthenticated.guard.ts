import { inject } from '@angular/core';
import { type CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { take, map, tap } from 'rxjs';

export const IsUnauthenticatedGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.user$.pipe(
    take(1),
    map((user) => (!user ? true : router.createUrlTree(['/home']))),
  );
};
