import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ChipModule } from 'primeng/chip';
import { CarouselModule } from 'primeng/carousel';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TagModule } from 'primeng/tag';
import { AccommodationOrder } from '../../interfaces/orders/AccommodationOrder';
import { CheckoutService } from '../checkout/services/checkout.service';
import { RouterUtils } from '../../utils/RouterUtils';
import { AccommodationDetailsViewComponent } from './ui/accommodation-details-view/accommodation-details-view.component';
import { AuthService } from '../../services/auth.service';
import { map, Observable } from 'rxjs';
import { UserRole } from '../../interfaces/model/User';

@Component({
  selector: 'smt-accommodation-details',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    TableModule,
    ChipModule,
    CarouselModule,
    ButtonModule,
    ToastModule,
    DropdownModule,
    InputNumberModule,
    ReactiveFormsModule,
    TagModule,
    AccommodationDetailsViewComponent,
  ],
  templateUrl: './accommodation-details.component.html',
  styles: ``,
})
export class AccommodationDetailsComponent implements OnInit {
  // Local variables
  form!: FormGroup;

  // Status variables
  accommodationId!: string;
  isAgentView$!: Observable<boolean | null>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private location = inject(Location);
  private authService = inject(AuthService);
  private checkoutService = inject(CheckoutService);

  ngOnInit() {
    // Fetch accommodation data
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id.length === 24) {
      this.accommodationId = id;
    } else this.router.navigate(['/home']);
    // Fetch user
    this.isAgentView$ = this.authService.user$.pipe(
      map(
        (user) => user && [UserRole.AGENT, UserRole.ADMIN].includes(user.role),
      ),
    );
  }

  handleNavigateBack(): void {
    if (window.history.length > 1) {
      this.location.back();
      return;
    } else this.router.navigate(['/home/stays']);
  }

  handleCheckout(accommodationOrder: AccommodationOrder) {
    // Save order details and move to checkout page
    this.checkoutService.setAccommodation(accommodationOrder);
    this.checkoutService.setReturnUrl(
      RouterUtils.getCleanedUrl(this.router, this.router.url),
    );
    this.router.navigate(['/checkout']);
  }
}
