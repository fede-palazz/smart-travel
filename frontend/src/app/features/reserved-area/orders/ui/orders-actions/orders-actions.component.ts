import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import {
  OrderPreview,
  OrderType,
} from '../../../../../interfaces/orders/Order';

@Component({
  selector: 'smt-orders-actions',
  standalone: true,
  imports: [CommonModule, MenuModule, ButtonModule],
  templateUrl: './orders-actions.component.html',
  styles: ``,
})
export class OrdersActionsComponent {
  // Local variables
  items: MenuItem[] | undefined;

  // State variables
  @Input({ required: true }) order!: OrderPreview;

  // Events
  @Output() onViewDetails = new EventEmitter<string>();
  @Output() onViewPackage = new EventEmitter<string>();

  ngOnInit() {
    this.items = [
      {
        label: 'View order details',
        icon: 'pageview',
        callback: () => this.handleViewDetails(),
      },
    ];
    // Check if it is related to an agency package
    if (this.order.type === OrderType.AGENCY) {
      this.items.push({
        label: 'View package details',
        icon: 'pageview',
        callback: () => this.handleViewPackage(),
      });
    }
  }

  handleViewDetails() {
    this.onViewDetails.emit(this.order.id);
  }

  handleViewPackage() {
    this.onViewPackage.emit(this.order.agencyPackageId);
  }
}
