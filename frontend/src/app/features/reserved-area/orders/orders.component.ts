import { Component, inject } from '@angular/core';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { OrderFilter } from '../../../interfaces/filters/OrderFilter';
import {
  BehaviorSubject,
  debounceTime,
  map,
  Observable,
  switchMap,
} from 'rxjs';
import { OrderPreview } from '../../../interfaces/orders/Order';
import { PagedRes } from '../../../interfaces/PagedRes';
import { QueryResult } from '../../../interfaces/QueryResult';
import { OrderService } from './services/order.service';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { NotFoundComponent } from '../../../shared/not-found.component';
import { DataViewModule } from 'primeng/dataview';
import { AgencyPackagesTableSkeletonComponent } from '../agency-packages/ui/agency-packages-table-skeleton/agency-packages-table-skeleton.component';
import { OrdersTableComponent } from './ui/orders-table/orders-table.component';
import { Router } from '@angular/router';
import { OrdersToolbarComponent } from './ui/orders-toolbar/orders-toolbar.component';
import { OrdersFilterComponent } from './ui/orders-filter/orders-filter.component';
import { OrdersDetailsComponent } from './ui/orders-details/orders-details.component';

@Component({
  selector: 'smt-orders',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    CardModule,
    NotFoundComponent,
    DataViewModule,
    AgencyPackagesTableSkeletonComponent,
    OrdersTableComponent,
    OrdersToolbarComponent,
    OrdersFilterComponent,
    OrdersDetailsComponent,
  ],
  templateUrl: './orders.component.html',
  styles: `
    ::ng-deep .p-dataview .p-dataview-header {
      background-color: inherit;
      border: 0;
      padding-inline: 8px;
    }

    ::ng-deep .p-dataview .p-dataview-emptymessage {
      display: none;
    }
  `,
})
export class OrdersComponent {
  // Status variables
  displayFilters = false; // Filters popup
  pageSize = 5;
  orderDetailsId?: string;

  filterSubject = new BehaviorSubject<{
    filterParams: OrderFilter;
    paginationParams: PaginationFilter;
  }>({ filterParams: {}, paginationParams: { size: this.pageSize } });
  filters$!: Observable<{
    filterParams: OrderFilter;
    paginationParams: PaginationFilter;
  }>;
  filterCounter$!: Observable<number>;
  orders$!: Observable<QueryResult<PagedRes<OrderPreview>>>;

  // Injectables
  private router = inject(Router);
  private orderService = inject(OrderService);

  ngOnInit(): void {
    this.filters$ = this.filterSubject.asObservable();
    this.filterCounter$ = this.filters$.pipe(
      map(({ filterParams }) => this.getFilterCounter(filterParams)),
    );
    this.orders$ = this.filters$.pipe(
      debounceTime(300), // wait 300ms after last emission
      switchMap(({ filterParams, paginationParams }) =>
        this.fetchOrders(filterParams, paginationParams),
      ),
    );
  }

  handleFilter(filters: OrderFilter) {
    const currentFilters = this.filterSubject.getValue().filterParams;
    const customerName = currentFilters.customerName;

    // Explicitly handle undefined values by removing them
    Object.keys(filters).forEach((key) => {
      const typedKey = key as keyof OrderFilter;
      const newFilter = filters[typedKey];

      if (newFilter === undefined || newFilter === null || newFilter === '') {
        delete currentFilters[typedKey];
      } else {
        currentFilters[typedKey] = newFilter as any;
      }
    });

    // Update filters and remove pagination filter
    this.filterSubject.next({
      filterParams: {
        ...currentFilters,
        ...(customerName !== undefined ? { customerName: customerName } : {}), // Add search bar value only if defined
      },
      paginationParams: { size: this.pageSize },
    });
  }

  handleSearch(query: string) {
    const currentFilters = this.filterSubject.getValue().filterParams;

    if (!query) {
      delete currentFilters['customerName'];
    }

    this.filterSubject.next({
      filterParams: {
        ...currentFilters,
        ...(query ? { customerName: query } : {}), // Add search bar value only if defined
      },
      paginationParams: { size: this.pageSize },
    });
  }

  handleViewDetails(id: string) {
    this.orderDetailsId = id;
  }

  handleCloseDetails() {
    this.orderDetailsId = undefined;
  }

  handleViewPackageDetails(id: string) {
    this.router.navigate(['/agency-packages', id]);
  }

  handlePageChange(page: number): void {
    const currentFilters = this.filterSubject.getValue();
    this.filterSubject.next({
      filterParams: {
        ...currentFilters.filterParams,
      },
      paginationParams: { page, size: currentFilters.paginationParams.size },
    });
  }

  /**
   * PRIVATE METHODS
   */

  private fetchOrders(
    filters?: OrderFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<OrderPreview>>> {
    return this.orderService.getOrders(filters, pagination);
  }

  private getFilterCounter(filterParams: OrderFilter): number {
    let counter = 0;

    if (filterParams.orderId) counter++;
    if (filterParams.customerId) counter++;
    if (filterParams.status) counter++;
    if (filterParams.type) counter++;
    if (filterParams.minAmount || filterParams.maxAmount) counter++;

    return counter;
  }
}
