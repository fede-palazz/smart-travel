import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { CardModule } from 'primeng/card';
import { OrderPreview } from '../../interfaces/orders/Order';
import { Observable } from 'rxjs';
import { PagedRes } from '../../interfaces/PagedRes';
import { QueryResult } from '../../interfaces/QueryResult';
import { AgencyPackagesTableSkeletonComponent } from '../reserved-area/agency-packages/ui/agency-packages-table-skeleton/agency-packages-table-skeleton.component';
import { NotFoundComponent } from '../../shared/not-found.component';
import { OrderService } from '../reserved-area/orders/services/order.service';
import { PrivateOrdersListComponent } from './ui/private-orders-list/private-orders-list.component';
import { OrdersDetailsComponent } from '../reserved-area/orders/ui/orders-details/orders-details.component';

@Component({
  selector: 'smt-private-orders',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    AgencyPackagesTableSkeletonComponent,
    NotFoundComponent,
    PrivateOrdersListComponent,
    OrdersDetailsComponent,
  ],
  templateUrl: './private-orders.component.html',
  styles: ``,
})
export class PrivateOrdersComponent {
  // Status variables
  orders$!: Observable<QueryResult<PagedRes<OrderPreview>>>;
  displayedOrderId?: string;

  // Injectables
  private orderService = inject(OrderService);

  ngOnInit(): void {
    this.orders$ = this.orderService.getOrders({}, { size: 20 });
  }

  handleViewDetails(orderId: string) {
    console.log(orderId);
    this.displayedOrderId = orderId;
  }

  handleClose() {
    this.displayedOrderId = undefined;
  }
}
