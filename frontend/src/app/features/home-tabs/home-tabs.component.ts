import { Component, inject, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TabMenuModule } from 'primeng/tabmenu';
import { MenuItem } from 'primeng/api';
import { CardModule } from 'primeng/card';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { FormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { NavigationEnd, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FlightTabComponent } from './ui/flight-tab/flight-tab.component';
import { AccommodationTabComponent } from './ui/accommodation-tab/accommodation-tab.component';
import { ActivityTabComponent } from './ui/activity-tab/activity-tab.component';
import { FlightQueryParams } from '../../interfaces/params/FlightQueryParams';
import { AccommodationQueryParams } from '../../interfaces/params/AccommodationQueryParams';
import { ActivityQueryParams } from '../../interfaces/params/ActivityQueryParams';
import { AgencyPackageTabComponent } from './ui/agency-package-tab/agency-package-tab.component';
import { CustomPackageTabComponent } from './ui/custom-package-tab/custom-package-tab.component';
import { CustomPackageQueryParams } from '../../interfaces/params/CustomPackageQueryParams';
import { AgencyPackageQueryParams } from '../../interfaces/params/AgencyPackageQueryParams';

@Component({
  selector: 'smt-home-tabs',
  standalone: true,
  imports: [
    TabMenuModule,
    ButtonModule,
    CardModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    CalendarModule,
    FormsModule,
    DropdownModule,
    CommonModule,
    FlightTabComponent,
    AccommodationTabComponent,
    ActivityTabComponent,
    AgencyPackageTabComponent,
    CustomPackageTabComponent,
  ],
  templateUrl: './home-tabs.component.html',
  styles: ``,
})
export class HomeTabsComponent implements OnInit {
  // Local variables
  items: MenuItem[];

  // State variables
  activeItem?: MenuItem;
  showCustomPackageTab: boolean = false;

  // Injectables
  private router = inject(Router);

  constructor() {
    // Initialize tabs sections
    this.items = [
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
        icon: 'apartment',
        url: '/home/stays',
      },
      {
        label: 'Activities',
        icon: 'local_activity',
        url: '/home/activities',
      },
    ];
  }

  ngOnInit() {
    // Set current active tab based on route
    this.activeItem = this.getActiveItem(this.items, this.router.url);

    // Listen to route changes to update active tab
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.activeItem = this.getActiveItem(this.items, this.router.url);
      }
    });
  }

  private getActiveItem = (items: MenuItem[], route: string) =>
    items.find((item) => route.startsWith(item.url!)) || this.items[0];

  handleActiveItemChange(item: MenuItem) {
    this.activeItem = item;
    // Reset package tab view
    this.showCustomPackageTab = false;
  }

  handleFlightSearch(queryParams: FlightQueryParams) {
    this.router.navigate(['/flights'], { queryParams });
  }

  handleAccommodationSearch(queryParams: AccommodationQueryParams) {
    this.router.navigate(['/stays'], { queryParams });
  }

  handleActivitySearch(queryParams: ActivityQueryParams) {
    this.router.navigate(['/activities'], { queryParams });
  }

  handleAgencyPackageSearch(queryParams: AgencyPackageQueryParams) {
    this.router.navigate(['/agency-packages'], { queryParams });
  }

  handleCustomPackageSearch(queryParams: CustomPackageQueryParams) {
    this.router.navigate(['/custom-packages'], { queryParams });
  }

  handleViewCustomizedPackage() {
    this.showCustomPackageTab = true;
  }

  handleViewAgencyPackage() {
    this.showCustomPackageTab = false;
  }
}
