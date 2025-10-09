import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { SlideshowComponent } from '../../../../shared/slideshow.component';
import { TextComponent } from '../../../../shared/text.component';
import { AgencyPackage } from '../../../../interfaces/model/AgencyPackage';
import { AgencyPackageDetailsFlightComponent } from '../agency-package-details-flight/agency-package-details-flight.component';
import { AgencyPackageDetailsAccommodationComponent } from '../agency-package-details-accommodation/agency-package-details-accommodation.component';
import { AccommodationOrder } from '../../../../interfaces/orders/AccommodationOrder';
import { Router } from '@angular/router';
import { AgencyPackageDetailsActivityComponent } from '../agency-package-details-activity/agency-package-details-activity.component';
import { ActivityOrder } from '../../../../interfaces/orders/ActivityOrder';
import { User } from '../../../../interfaces/model/User';
import { DividerModule } from 'primeng/divider';

@Component({
  selector: 'smt-agency-package-details-card',
  standalone: true,
  imports: [
    CardModule,
    CommonModule,
    SlideshowComponent,
    TextComponent,
    ReactiveFormsModule,
    TableModule,
    DropdownModule,
    ButtonModule,
    TagModule,
    DividerModule,
    AgencyPackageDetailsFlightComponent,
    AgencyPackageDetailsAccommodationComponent,
    AgencyPackageDetailsActivityComponent,
  ],
  templateUrl: './agency-package-details-card.component.html',
  styles: ``,
})
export class AgencyPackageDetailsCardComponent {
  // Status variables
  @Input({ required: true }) agencyPackage!: AgencyPackage;
  @Input() isAgentView: boolean = false;
  isCheckingOut: boolean = false;

  // Events
  @Output() onCheckout = new EventEmitter<string>();

  // Injectables
  private router = inject(Router);

  handleViewAccommodation(accommodation: AccommodationOrder) {
    this.router.navigate(['/stays', accommodation.accommodationId]);
  }

  handleViewActivity(activity: ActivityOrder) {
    this.router.navigate(['/activities', activity.activityId]);
  }

  handleCheckout() {
    this.isCheckingOut = true;
    this.onCheckout.emit(this.agencyPackage.id);
  }
}
