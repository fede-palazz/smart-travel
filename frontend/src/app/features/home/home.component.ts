import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { HomeTabsComponent } from '../home-tabs/home-tabs.component';
import { DestinationsCarouselComponent } from './ui/destinations-carousel/destinations-carousel.component';
import { Observable } from 'rxjs';
import { DestinationPreview } from '../../interfaces/model/Destination';
import { DestinationService } from './services/destination.service';
import { CommonModule } from '@angular/common';
import { CheckoutService } from '../checkout/services/checkout.service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { PaymentStatus } from '../../interfaces/orders/Order';
import { ActivatedRoute, Router } from '@angular/router';
import { QueryResult } from '../../interfaces/QueryResult';
import { PagedRes } from '../../interfaces/PagedRes';
import { DestinationsSkeletonComponent } from './ui/destinations-skeleton/destinations-skeleton.component';
import { NotFoundComponent } from '../../shared/not-found.component';

@Component({
  selector: 'smt-home',
  standalone: true,
  imports: [
    HomeTabsComponent,
    DestinationsCarouselComponent,
    CommonModule,
    ToastModule,
    DestinationsSkeletonComponent,
    NotFoundComponent,
  ],
  templateUrl: './home.component.html',
  styles: ``,
})
export class HomeComponent implements OnInit, AfterViewInit {
  // State variables
  destinationsPreviews$!: Observable<QueryResult<PagedRes<DestinationPreview>>>;

  // Injectables
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private destinationService = inject(DestinationService);
  private checkoutService = inject(CheckoutService);
  private messageService = inject(MessageService);

  ngOnInit() {
    // Fetch destinations' previews
    this.destinationsPreviews$ =
      this.destinationService.getDestinationsPreviews();
    // Reset order store
    this.checkoutService.clearOrder();
  }

  ngAfterViewInit(): void {
    // Check for payment status param
    const paymentStatus = this.route.snapshot.queryParams['paymentStatus'];
    const isValidPaymentStatus = Object.values(PaymentStatus).includes(
      paymentStatus as PaymentStatus,
    );
    if (isValidPaymentStatus) {
      this.displayPaymentStatus(paymentStatus);
    }
  }

  displayPaymentStatus(status: PaymentStatus) {
    console.log(status);
    switch (status) {
      case PaymentStatus.PAID:
        this.messageService.add({
          severity: 'success',
          summary: 'Order in progress',
          detail:
            'Your order is currently being processed. You will receive an email once it is complete.',
        });
        break;
      case PaymentStatus.CANCELLED:
        this.messageService.add({
          severity: 'warn',
          summary: 'Order cancelled',
          detail:
            'You have cancelled your order. If this was a mistake, please place a new order or contact support.',
        });
        break;
      default:
        break;
    }
  }

  handleViewDestination(destination: DestinationPreview) {
    const now = new Date();

    // Start of current month at 00:00:00
    const startDate = new Date(now.getFullYear(), now.getMonth(), 1);
    // Last day of current month at 00:00:00
    const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    this.router.navigate(['/agency-packages'], {
      queryParams: {
        to: destination.city,
        toType: 'city',
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString(),
      },
    });
  }
}
