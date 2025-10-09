import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  OrderType,
  PaymentStatus,
} from '../../../../../interfaces/orders/Order';
import { OrderFilter } from '../../../../../interfaces/filters/OrderFilter';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { SelectButtonModule } from 'primeng/selectbutton';
import { SliderModule } from 'primeng/slider';
import { DialogContainerComponent } from '../../../../../shared/dialog-container.component';

@Component({
  selector: 'smt-orders-filter',
  standalone: true,
  imports: [
    CommonModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    SliderModule,
    FormsModule,
    SelectButtonModule,
    DialogContainerComponent,
  ],
  templateUrl: './orders-filter.component.html',
  styles: ``,
})
export class OrdersFilterComponent {
  // Local variables
  minPrice: number = 100;
  maxPrice: number = 15000;
  typeOptions = Object.values(OrderType).map((status) => ({
    label: status,
    value: status,
  }));
  paymentStatusOptions = Object.values(PaymentStatus)
    .filter(
      (value) =>
        value !== PaymentStatus.EXPIRED && value !== PaymentStatus.PAID,
    )
    .map((status) => ({
      label: status,
      value: status,
    }));

  // Status variables
  @Input({ required: true }) isVisible!: boolean;

  orderId?: string;
  customerId?: string;
  type?: string;
  paymentStatus?: string;
  priceRange: number[] = [this.minPrice, this.maxPrice];

  // Events
  @Output() onClose = new EventEmitter();
  @Output() onFilter = new EventEmitter<OrderFilter>();

  handleFilter() {
    const filters: OrderFilter = {
      orderId: this.orderId ? this.orderId : undefined,
      customerId: this.customerId ? this.customerId : undefined,
      status: this.paymentStatus ? this.paymentStatus : undefined,
      type: this.type ? this.type : undefined,
      minAmount:
        this.priceRange[0] !== this.minPrice ? this.priceRange[0] : undefined,
      maxAmount:
        this.priceRange[1] !== this.maxPrice ? this.priceRange[1] : undefined,
    };
    this.onFilter.emit(filters);
    this.onClose.emit();
  }

  handleReset() {
    this.orderId = undefined;
    this.customerId = undefined;
    this.paymentStatus = undefined;
    this.priceRange = [this.minPrice, this.maxPrice];
    this.paymentStatus = undefined;
    this.type = undefined;
    this.onFilter.emit({});
  }

  handleClose() {
    this.onClose.emit();
  }
}
