import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { TextComponent } from '../../../../shared/text.component';
import { ActivityFiltersComponent } from '../activity-filters/activity-filters.component';
import { ActivityListComponent } from '../activity-list/activity-list.component';
import { ActivityQueryParams } from '../../../../interfaces/params/ActivityQueryParams';
import { ActivityFilter } from '../../../../interfaces/filters/ActivityFilter';
import { Activity } from '../../../../interfaces/model/Activity';
import { Observable, switchMap, tap } from 'rxjs';
import { PaginationFilter } from '../../../../interfaces/filters/PaginationFilter';
import { PagedRes } from '../../../../interfaces/PagedRes';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { ActivityService } from '../../services/activity.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NotFoundComponent } from '../../../../shared/not-found.component';
import { ActivitySkeletonComponent } from '../activity-skeleton/activity-skeleton.component';

@Component({
  selector: 'smt-activity-view',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    TextComponent,
    ActivityFiltersComponent,
    ActivityListComponent,
    NotFoundComponent,
    ActivitySkeletonComponent,
  ],
  templateUrl: './activity-view.component.html',
  styles: `
    ::ng-deep .p-accordion .p-accordion-header .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
  `,
})
export class ActivityViewComponent {
  // Local variables
  sortFields = ['bla']; // TODO: fill
  accordionItemIndex: number = -1; // Search accordion state

  // Status variables
  @Input({ required: true }) params$!: Observable<{
    searchParams: ActivityQueryParams;
    paginationParams: PaginationFilter;
    filterParams: ActivityFilter;
  }>;
  activities$!: Observable<QueryResult<PagedRes<Activity>>>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private activityService = inject(ActivityService);

  // Events
  @Output() onSelectActivity = new EventEmitter<Activity>();

  ngOnInit(): void {
    // Fetch accommodations every time query params change
    this.activities$ = this.params$.pipe(
      switchMap(({ searchParams, filterParams, paginationParams }) =>
        this.fetchActivities(searchParams, filterParams, paginationParams),
      ),
      tap(() => {
        // Close the accordion after a new search
        this.accordionItemIndex = -1;
      }),
    );
  }

  handleFilter(filterParams: ActivityFilter): void {
    const currentParams = { ...this.route.snapshot.queryParams };

    // Explicitly handle undefined values by removing them
    Object.keys(filterParams).forEach((key) => {
      const typedKey = key as keyof ActivityFilter;
      if (filterParams[typedKey] === undefined) {
        delete currentParams[typedKey];
      } else {
        currentParams[typedKey] = filterParams[typedKey];
      }
    });

    // Reset pagination params when filtering
    const paginationKeys: (keyof PaginationFilter)[] = [
      'page',
      'size',
      'sort',
      'order',
    ];
    paginationKeys.forEach((key) => delete currentParams[key]);

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        ...currentParams,
      },
    });
  }

  handlePageChange(page: number): void {
    const currentParams = { ...this.route.snapshot.queryParams };

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        ...currentParams,
        page,
      },
      queryParamsHandling: 'merge',
    });
  }

  handleSelectActivity(activity: Activity) {
    this.onSelectActivity.emit(activity);
  }

  /**
   * PRIVATE METHODS
   */

  private fetchActivities(
    searchParams: ActivityQueryParams,
    filters?: ActivityFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<Activity>>> {
    // Get accommodations
    return this.activityService.getActivities(
      searchParams.to,
      searchParams.toType,
      searchParams.startDate,
      searchParams.endDate,
      filters,
      pagination,
    );
  }
}
