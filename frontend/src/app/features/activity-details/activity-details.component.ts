import { Component, inject, OnInit } from '@angular/core';
import { Activity } from '../../interfaces/model/Activity';
import { Router, ActivatedRoute } from '@angular/router';
import { CheckoutService } from '../checkout/services/checkout.service';
import { ActivityDetailsService } from './services/activity-details.service';
import { CommonModule, Location } from '@angular/common';
import { ActivityOrder } from '../../interfaces/orders/ActivityOrder';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ReactiveFormsModule } from '@angular/forms';
import { CarouselModule } from 'primeng/carousel';
import { ChipModule } from 'primeng/chip';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { TableModule } from 'primeng/table';
import { map, Observable } from 'rxjs';
import { QueryResult } from '../../interfaces/QueryResult';
import { ActivityDetailsViewComponent } from './ui/activity-details-view/activity-details-view.component';
import { AuthService } from '../../services/auth.service';
import { UserRole } from '../../interfaces/model/User';

@Component({
  selector: 'smt-activity-details',
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
    ActivityDetailsViewComponent,
  ],
  templateUrl: './activity-details.component.html',
  styles: ``,
})
export class ActivityDetailsComponent implements OnInit {
  // Status variables
  activityId!: string;
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
    if (!id || id.length !== 24) {
      this.router.navigate(['/home']);
      return;
    }
    this.activityId = id;
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
    } else this.router.navigate(['/home/activities']);
  }

  handleCheckout(activity: ActivityOrder) {
    // Save order details and move to checkout page
    this.checkoutService.addActivity(activity);
    this.router.navigate(['/checkout']);
  }
}
