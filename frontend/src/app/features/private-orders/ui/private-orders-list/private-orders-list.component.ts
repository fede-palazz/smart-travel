import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DataViewModule } from 'primeng/dataview';
import { OrderPreview, OrderType } from '../../../../interfaces/orders/Order';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'smt-private-orders-list',
  standalone: true,
  imports: [CommonModule, DataViewModule, TagModule, ButtonModule],
  templateUrl: './private-orders-list.component.html',
  styles: ``,
})
export class PrivateOrdersListComponent {
  @Input({ required: true }) orders!: OrderPreview[];

  @Output() onViewDetails = new EventEmitter<string>();

  handleViewDetails(order: OrderPreview) {
    this.onViewDetails.emit(order.id);
  }

  getSeverity(type: OrderType) {
    switch (type) {
      case OrderType.AGENCY:
        return 'success';

      case OrderType.CUSTOM:
        return 'secondary';

      case OrderType.SINGLE:
        return 'info';

      default:
        return 'danger';
    }
  }
}
