import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { TextComponent } from '../../../../shared/text.component';
import { AccommodationFiltersComponent } from '../accommodation-filters/accommodation-filters.component';
import { AccommodationListComponent } from '../accommodation-list/accommodation-list.component';
import { AccommodationQueryParams } from '../../../../interfaces/params/AccommodationQueryParams';
import { AccommodationPreview } from '../../../../interfaces/model/AccommodationPreview';
import { AccommodationService } from '../../services/accommodation.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, switchMap, tap } from 'rxjs';
import { PaginationFilter } from '../../../../interfaces/filters/PaginationFilter';
import { PagedRes } from '../../../../interfaces/PagedRes';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { AccommodationFilter } from '../../../../interfaces/filters/AccommodationFilter';
import { NotFoundComponent } from '../../../../shared/not-found.component';
import { AccommodationSkeletonComponent } from '../accommodation-skeleton/accommodation-skeleton.component';

@Component({
  selector: 'smt-accommodation-view',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    TextComponent,
    AccommodationFiltersComponent,
    AccommodationListComponent,
    NotFoundComponent,
    AccommodationSkeletonComponent,
  ],
  templateUrl: './accommodation-view.component.html',
  styles: `
    ::ng-deep .p-accordion .p-accordion-header .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
  `,
})
export class AccommodationViewComponent implements OnInit {
  // Local variables
  accordionItemIndex: number = -1; // Search accordion state
  sortFields = ['bla']; // TODO: fill

  // Status variables
  @Input({ required: true }) params$!: Observable<{
    searchParams: AccommodationQueryParams;
    paginationParams: PaginationFilter;
    filterParams: AccommodationFilter;
  }>;
  accommodations$!: Observable<QueryResult<PagedRes<AccommodationPreview>>>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private accommodationService = inject(AccommodationService);

  // Events
  @Output() onSelectAccommodation = new EventEmitter<AccommodationPreview>();

  ngOnInit(): void {
    // Fetch accommodations every time query params change
    this.accommodations$ = this.params$.pipe(
      switchMap(({ searchParams, filterParams, paginationParams }) =>
        this.fetchAccommodations(searchParams, filterParams, paginationParams),
      ),
      tap(() => {
        // Close the accordion after a new search
        this.accordionItemIndex = -1;
      }),
    );
  }

  handleFilter(filterParams: AccommodationFilter): void {
    const currentParams = { ...this.route.snapshot.queryParams };

    // Explicitly handle undefined values by removing them
    Object.keys(filterParams).forEach((key) => {
      const typedKey = key as keyof AccommodationFilter;
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

  handleSelectAccommodation(accommodation: AccommodationPreview) {
    this.onSelectAccommodation.emit(accommodation);
  }

  /**
   * PRIVATE METHODS
   */

  private fetchAccommodations(
    searchParams: AccommodationQueryParams,
    filterParams?: AccommodationFilter,
    paginationParams?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<AccommodationPreview>>> {
    return this.accommodationService.getAccommodations(
      searchParams.to,
      searchParams.toType,
      searchParams.startDate,
      searchParams.endDate,
      searchParams.quantity,
      filterParams,
      paginationParams,
    );
  }
}
