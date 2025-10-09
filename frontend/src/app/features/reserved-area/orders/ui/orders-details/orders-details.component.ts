import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { DialogContainerComponent } from '../../../../../shared/dialog-container.component';
import { Order } from '../../../../../interfaces/orders/Order';
import { OrderService } from '../../services/order.service';
import { Observable } from 'rxjs';
import { QueryResult } from '../../../../../interfaces/QueryResult';
import { CommonModule } from '@angular/common';
import { NotFoundComponent } from '../../../../../shared/not-found.component';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { OrdersDetailsItemsComponent } from '../orders-details-items/orders-details-items.component';

@Component({
  selector: 'smt-orders-details',
  standalone: true,
  imports: [
    DialogContainerComponent,
    CommonModule,
    NotFoundComponent,
    ProgressSpinnerModule,
    OrdersDetailsItemsComponent,
  ],
  templateUrl: './orders-details.component.html',
  styles: ``,
})
export class OrdersDetailsComponent implements OnInit {
  // Status variables
  @Input({ required: true }) orderId?: string;
  order$!: Observable<QueryResult<Order>>;

  // Injectables
  private ordersService = inject(OrderService);

  // Events
  @Output() onClose = new EventEmitter();

  ngOnInit(): void {
    this.order$ = this.ordersService.getOrderById(this.orderId!);
  }

  handleClose() {
    this.onClose.emit();
  }
}
