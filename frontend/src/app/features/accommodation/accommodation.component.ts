import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { AccommodationPreview } from '../../interfaces/model/AccommodationPreview';
import { map, Observable } from 'rxjs';
import { AccommodationFilter } from '../../interfaces/filters/AccommodationFilter';
import { PaginationFilter } from '../../interfaces/filters/PaginationFilter';
import { AccommodationTabComponent } from '../home-tabs/ui/accommodation-tab/accommodation-tab.component';
import { AccommodationQueryParams } from '../../interfaces/params/AccommodationQueryParams';
import { AccommodationViewComponent } from './ui/accommodation-view/accommodation-view.component';

@Component({
  selector: 'smt-accommodation',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    AccommodationTabComponent,
    AccommodationViewComponent,
  ],
  templateUrl: './accommodation.component.html',
  styles: `
    ::ng-deep .p-accordion .p-accordion-header .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
  `,
})
export class AccommodationComponent {
  // Local variables
  accommodationParams$!: Observable<{
    searchParams: AccommodationQueryParams;
    paginationParams: PaginationFilter;
    filterParams: AccommodationFilter;
  }>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    // Extract query params on route change
    this.accommodationParams$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractSearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractFilterParams(params),
      })),
    );
  }

  handleAccommodationSearch(queryParams: AccommodationQueryParams) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
    });
  }

  handleSelectAccommodation(accommodation: AccommodationPreview) {
    const startDate = this.route.snapshot.queryParamMap.get('startDate') ?? '';
    const endDate = this.route.snapshot.queryParamMap.get('endDate') ?? '';

    this.router.navigate(['/stays', accommodation.id], {
      queryParams: {
        startDate,
        endDate,
      },
    });
  }

  /**
   * PRIVATE METHODS
   */

  private extractSearchParams(params: ParamMap): AccommodationQueryParams {
    return {
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
      quantity: Number(params.get('quantity')) || 1,
    };
  }

  private extractPaginationParams(params: ParamMap): PaginationFilter {
    return {
      page: Number(params.get('page')) || 0,
      size: Number(params.get('size')) || 5,
      sort: params.get('sort') ?? undefined,
      order: params.get('order') ?? undefined,
    };
  }

  private extractFilterParams(params: ParamMap): AccommodationFilter {
    return {
      name: params.get('name') ?? undefined,
      types: params.getAll('types') ?? [],
      services: params.getAll('services') ?? [],
      minDistanceToCenterKm:
        Number(params.get('minDistanceToCenterKm')) || undefined,
      maxDistanceToCenterKm:
        Number(params.get('maxDistanceToCenterKm')) || undefined,
      minPricePerNight: Number(params.get('minPricePerNight')) || undefined,
      maxPricePerNight: Number(params.get('maxPricePerNight')) || undefined,
      minRating: Number(params.get('minRating')) || undefined,
    };
  }
}
