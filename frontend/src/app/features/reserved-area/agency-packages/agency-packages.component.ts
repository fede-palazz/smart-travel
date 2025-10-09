import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import {
  BehaviorSubject,
  debounceTime,
  map,
  Observable,
  switchMap,
} from 'rxjs';
import { AgencyPackagePreview } from '../../../interfaces/model/AgencyPackagePreview';
import { PagedRes } from '../../../interfaces/PagedRes';
import { QueryResult } from '../../../interfaces/QueryResult';
import { AgencyPackagesService } from './services/agency-packages.service';
import { AgencyPackagesTableComponent } from './ui/agency-packages-table/agency-packages-table.component';
import { DataViewModule } from 'primeng/dataview';
import { AgencyPackagesCardComponent } from './ui/agency-packages-card/agency-packages-card.component';
import { CardModule } from 'primeng/card';
import { AgencyPackagesToolbarComponent } from './ui/agency-packages-toolbar/agency-packages-toolbar.component';
import { AgencyPackagesCardSkeletonComponent } from './ui/agency-packages-card-skeleton/agency-packages-card-skeleton.component';
import { AgencyPackagesTableSkeletonComponent } from './ui/agency-packages-table-skeleton/agency-packages-table-skeleton.component';
import { AgencyPackagesFilterComponent } from './ui/agency-packages-filter/agency-packages-filter.component';
import { FullAgencyPackageFilter } from '../../../interfaces/filters/AgencyPackageFilter';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { Router } from '@angular/router';
import { NotFoundComponent } from '../../../shared/not-found.component';
import { AgencyPackagesSearchComponent } from './ui/agency-packages-search/agency-packages-search.component';
import { AgencyPackageNewQueryParams } from '../../../interfaces/params/AgencyPackageNewQueryParams';

@Component({
  selector: 'smt-agency-packages',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    DataViewModule,
    AgencyPackagesCardComponent,
    CardModule,
    AgencyPackagesToolbarComponent,
    AgencyPackagesCardSkeletonComponent,
    AgencyPackagesTableComponent,
    AgencyPackagesTableSkeletonComponent,
    AgencyPackagesFilterComponent,
    ToastModule,
    NotFoundComponent,
    AgencyPackagesSearchComponent,
  ],
  templateUrl: './agency-packages.component.html',
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
export class AgencyPackagesComponent implements OnInit {
  // Status variables
  dvLayout: 'grid' | 'list' = 'list'; // Data view layout
  displayFilters = false; // Filters popup
  displayPackageSearchTab = false; // Add package search popup
  pageSize = 5;

  filterSubject = new BehaviorSubject<{
    filterParams: FullAgencyPackageFilter;
    paginationParams: PaginationFilter;
  }>({ filterParams: {}, paginationParams: { size: this.pageSize } });
  filters$!: Observable<{
    filterParams: FullAgencyPackageFilter;
    paginationParams: PaginationFilter;
  }>;
  filterCounter$!: Observable<number>;
  agencyPackages$!: Observable<QueryResult<PagedRes<AgencyPackagePreview>>>;

  // Injectables
  private agencyPackagesService = inject(AgencyPackagesService);
  private router = inject(Router);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.filters$ = this.filterSubject.asObservable();
    this.filterCounter$ = this.filters$.pipe(
      map(({ filterParams }) => this.getFilterCounter(filterParams)),
    );
    this.agencyPackages$ = this.filters$.pipe(
      debounceTime(300), // wait 300ms after last emission
      switchMap(({ filterParams, paginationParams }) =>
        this.fetchAgencyPackages(filterParams, paginationParams),
      ),
    );
  }

  handleLayoutChange(layout: 'list' | 'grid') {
    this.dvLayout = layout;
  }

  handleFilter(filters: FullAgencyPackageFilter) {
    const currentFilters = this.filterSubject.getValue().filterParams;
    const name = currentFilters.name;

    // Explicitly handle undefined values by removing them
    Object.keys(filters).forEach((key) => {
      const typedKey = key as keyof FullAgencyPackageFilter;
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
        ...(name !== undefined ? { name } : {}), // Add search bar value only if defined
      },
      paginationParams: { size: this.pageSize },
    });
  }

  handleSearch(query: string) {
    const currentFilters = this.filterSubject.getValue().filterParams;

    if (!query) {
      delete currentFilters['name'];
    }

    this.filterSubject.next({
      filterParams: {
        ...currentFilters,
        ...(query ? { name: query } : {}), // Add search bar value only if defined
      },
      paginationParams: { size: this.pageSize },
    });
  }

  handleAddPackageSearch(queryParams: AgencyPackageNewQueryParams) {
    this.router.navigate(['/dashboard/agency-packages/new'], { queryParams });
  }

  handleViewDetails(id: string) {
    this.router.navigate(['/agency-packages', id]);
  }

  handlePublishPackage(id: string) {
    const currentFilters = this.filterSubject.getValue();
    this.agencyPackagesService
      .publishPackage(
        id,
        currentFilters.filterParams,
        currentFilters.paginationParams,
      )
      .subscribe(({ data, error }) => {
        if (error) {
          console.error(error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error while performing the request',
            detail: error,
          });
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: 'Package Published',
            detail: 'The package has been successfully published',
          });
        }
      });
  }

  handleArchivePackage(id: string) {
    const currentFilters = this.filterSubject.getValue();
    this.agencyPackagesService
      .archivePackage(
        id,
        currentFilters.filterParams,
        currentFilters.paginationParams,
      )
      .subscribe(({ data, error }) => {
        if (error) {
          console.error(error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error while performing the request',
            detail: error,
          });
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: 'Package Archived',
            detail: 'The package has been successfully archived',
          });
        }
      });
  }

  handleDeletePackage(id: string) {
    const currentFilters = this.filterSubject.getValue();
    this.agencyPackagesService
      .deletePackage(
        id,
        currentFilters.filterParams,
        currentFilters.paginationParams,
      )
      .subscribe(({ data, error }) => {
        if (error) {
          console.error(error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error while performing the request',
            detail: error,
          });
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: 'Package Deleted',
            detail: 'The package has been successfully deleted',
          });
        }
      });
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

  private fetchAgencyPackages(
    filters?: FullAgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<AgencyPackagePreview>>> {
    return this.agencyPackagesService.getAgencyPackagesPreview(
      filters,
      pagination,
    );
  }

  private getFilterCounter(filterParams: FullAgencyPackageFilter): number {
    let counter = 0;

    if (filterParams.tags && filterParams.tags.length > 0) counter++;
    if (filterParams.status) counter++;
    if (filterParams.minPrice || filterParams.maxPrice) counter++;
    if (filterParams.authorId) counter++;
    if (filterParams.to && filterParams.toType) counter++;
    if (filterParams.startDate) counter++;
    if (filterParams.endDate) counter++;

    return counter;
  }
}
