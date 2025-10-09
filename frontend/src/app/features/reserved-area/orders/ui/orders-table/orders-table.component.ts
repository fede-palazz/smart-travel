import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import {
  OrderPreview,
  OrderType,
} from '../../../../../interfaces/orders/Order';
import { OrdersActionsComponent } from '../orders-actions/orders-actions.component';

@Component({
  selector: 'smt-orders-table',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    MenuModule,
    RippleModule,
    ButtonModule,
    PaginatorModule,
    OrdersActionsComponent,
  ],
  templateUrl: './orders-table.component.html',
  styles: `
    ::ng-deep .p-datatable-footer {
      padding: 0;
    }
  `,
})
export class OrdersTableComponent {
  // Status variables
  @Input({ required: true }) orders!: OrderPreview[];
  @Input({ required: true }) pageSize!: number;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;

  // Events
  @Output() onViewDetails = new EventEmitter<string>();
  @Output() onViewPackageDetails = new EventEmitter<string>();
  @Output() onPageChange = new EventEmitter<number>();

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

  handleViewDetails(id: string) {
    this.onViewDetails.emit(id);
  }

  handleViewPackage(id: string) {
    this.onViewPackageDetails.emit(id);
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }
}
