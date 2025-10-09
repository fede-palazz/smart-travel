import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { IsOrderPresentGuard } from './guards/is-order-present.guard';
import { IsUnauthenticatedGuard } from './guards/is-unauthenticated.guard';
import { isCustomerGuard } from './guards/is-customer.guard';
import { redirectLoginGuard } from './guards/redirect-login.guard';
import { isAgentOrAdminGuard } from './guards/is-agent-or-admin.guard';
import { isUnauthenticatedOrCustomerGuard } from './guards/is-unauthenticated-or-customer.guard';
import { isAdminGuard } from './guards/is-admin.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(
        (m) => m.LoginComponent,
      ),
    canActivate: [IsUnauthenticatedGuard],
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
    canActivate: [IsUnauthenticatedGuard],
  },
  {
    path: 'home',
    component: HomeComponent,
    children: [
      { path: '', redirectTo: 'packages', pathMatch: 'full' },
      {
        path: 'packages',
        component: HomeComponent,
      },
      {
        path: 'flights',
        component: HomeComponent,
      },
      {
        path: 'stays',
        component: HomeComponent,
      },
      {
        path: 'activities',
        component: HomeComponent,
      },
    ],
    canActivate: [isUnauthenticatedOrCustomerGuard],
  },
  {
    path: 'flights',
    loadComponent: () =>
      import('./features/flight/flight.component').then(
        (m) => m.FlightComponent,
      ),
    canActivate: [isUnauthenticatedOrCustomerGuard],
  },
  {
    path: 'stays',
    loadComponent: () =>
      import('./features/accommodation/accommodation.component').then(
        (m) => m.AccommodationComponent,
      ),
  },
  {
    path: 'stays/:id',
    loadComponent: () =>
      import(
        './features/accommodation-details/accommodation-details.component'
      ).then((m) => m.AccommodationDetailsComponent),
  },
  {
    path: 'activities',
    loadComponent: () =>
      import('./features/activity/activity.component').then(
        (m) => m.ActivityComponent,
      ),
  },
  {
    path: 'activities/:id',
    loadComponent: () =>
      import('./features/activity-details/activity-details.component').then(
        (m) => m.ActivityDetailsComponent,
      ),
  },
  {
    path: 'agency-packages',
    loadComponent: () =>
      import('./features/agency-package/agency-package.component').then(
        (m) => m.AgencyPackageComponent,
      ),
  },
  {
    path: 'agency-packages/:id',
    loadComponent: () =>
      import(
        './features/agency-package-details/agency-package-details.component'
      ).then((m) => m.AgencyPackageDetailsComponent),
  },
  {
    path: 'custom-packages',
    loadComponent: () =>
      import('./features/custom-package/custom-package.component').then(
        (m) => m.CustomPackageComponent,
      ),
  },
  {
    path: 'orders',
    loadComponent: () =>
      import('./features/private-orders/private-orders.component').then(
        (m) => m.PrivateOrdersComponent,
      ),
    canActivate: [isCustomerGuard],
  },
  {
    path: 'checkout',
    loadComponent: () =>
      import('./features/checkout/checkout.component').then(
        (m) => m.CheckoutComponent,
      ),
    canActivate: [redirectLoginGuard, isCustomerGuard, IsOrderPresentGuard],
  },

  /**
   * RESERVED ROUTES
   */
  // {
  //   path: 'dashboard',
  //   loadComponent: () =>
  //     import('./features/reserved-area/dashboard/dashboard.component').then(
  //       (m) => m.DashboardComponent,
  //     ),
  //   canActivate: [isAgentOrAdminGuard],
  // },
  {
    path: 'dashboard/agency-packages',
    loadComponent: () =>
      import(
        './features/reserved-area/agency-packages/agency-packages.component'
      ).then((m) => m.AgencyPackagesComponent),
    canActivate: [isAgentOrAdminGuard],
  },
  {
    path: 'dashboard/agency-packages/new',
    loadComponent: () =>
      import(
        './features/reserved-area/agency-packages-new/agency-packages-new.component'
      ).then((m) => m.AgencyPackagesNewComponent),
    canActivate: [isAgentOrAdminGuard],
  },
  {
    path: 'dashboard/orders',
    loadComponent: () =>
      import('./features/reserved-area/orders/orders.component').then(
        (m) => m.OrdersComponent,
      ),
    canActivate: [isAgentOrAdminGuard],
  },
  {
    path: 'dashboard/users',
    loadComponent: () =>
      import('./features/reserved-area/users/users.component').then(
        (m) => m.UsersComponent,
      ),
    canActivate: [isAdminGuard],
  },
  { path: '**', redirectTo: 'home' },
];
