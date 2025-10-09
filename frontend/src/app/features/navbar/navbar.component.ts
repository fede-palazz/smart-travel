import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { AvatarModule } from 'primeng/avatar';
import { RippleModule } from 'primeng/ripple';
import { MenuItem } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { InputIconModule } from 'primeng/inputicon';
import { ButtonModule } from 'primeng/button';
import { NavigationEnd, Router } from '@angular/router';
import { BrandComponent } from '../../shared/brand.component';
import { AuthService } from '../../services/auth.service';
import { User, UserRole } from '../../interfaces/model/User';
import { MenuModule } from 'primeng/menu';
import { TagModule } from 'primeng/tag';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'smt-navbar',
  standalone: true,
  imports: [
    MenubarModule,
    AvatarModule,
    CommonModule,
    RippleModule,
    InputIconModule,
    ButtonModule,
    BrandComponent,
    MenuModule,
    TagModule,
  ],
  templateUrl: './navbar.component.html',
  styles: ``,
})
export class NavbarComponent implements OnInit {
  // Local variables
  customerNavbarItems: MenuItem[] = [
    {
      label: 'Packages',
      icon: 'luggage',
      url: '/home/packages',
    },
    {
      label: 'Flights',
      icon: 'flight',
      url: '/home/flights',
    },
    {
      label: 'Stays',
      icon: 'home',
      url: '/home/stays',
    },
    {
      label: 'Activities',
      icon: 'local_activity',
      url: '/home/activities',
    },
  ];
  agentNavbarItems: MenuItem[] = [
    // {
    //   label: 'Dashboard',
    //   icon: 'dashboard',
    //   url: '/dashboard',
    // },
    {
      label: 'Packages',
      icon: 'luggage',
      url: '/dashboard/agency-packages',
    },
    {
      label: 'Orders',
      icon: 'shopping_cart',
      url: '/dashboard/orders',
    },
    {
      label: 'Users',
      icon: 'people',
      url: '/dashboard/users',
      adminOnly: true,
    },
  ];
  activeItem?: MenuItem;
  userMenuItems: MenuItem[] = [
    {
      label: 'Profile & Settings',
      items: [
        {
          label: 'Logout',
          icon: 'pi pi-sign-out',
          command: () => this.handleLogout(),
        },
      ],
    },
  ];
  role = UserRole;

  // State variables
  user: User | null = null;

  // Injectables
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);

  ngOnInit() {
    this.authService.user$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        this.user = user;
        if (user?.role === UserRole.CUSTOMER) {
          this.userMenuItems[0]!.items!.unshift(
            {
              label: 'My orders',
              icon: 'pi pi-shopping-cart',
              routerLink: '/orders',
            },
            {
              separator: true,
            },
          );
        }
      });

    // Set current active tab based on route
    this.updateActiveItem(this.router.url, this.customerNavbarItems);

    // Listen to route changes to update active tab
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateActiveItem(this.router.url, this.customerNavbarItems);
      }
    });
  }

  handleLogout() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  private updateActiveItem(route: string, navItems: MenuItem[]) {
    const item = this.getActiveItem(route, navItems);
    this.activeItem = item
      ? {
          ...item,
          items: this.getInactiveItems(item).map((item) => ({
            ...item,
          })),
        }
      : undefined;
  }

  private getActiveItem = (route: string, navItems: MenuItem[]) => {
    if (
      route.startsWith('/home') ||
      route.startsWith('/dashboard') ||
      route.startsWith('/orders')
    )
      return undefined;
    return navItems.find((item) =>
      route.includes(item.url!.split('/').pop() || ''),
    );
  };

  private getInactiveItems = (currentItem: MenuItem) => {
    return this.customerNavbarItems.filter((item) => item !== currentItem);
  };
}
