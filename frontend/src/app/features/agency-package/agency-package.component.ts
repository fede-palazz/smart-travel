import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { TextComponent } from '../../shared/text.component';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { map, Observable, switchMap, tap } from 'rxjs';
import { QueryResult } from '../../interfaces/QueryResult';
import { PagedRes } from '../../interfaces/PagedRes';
import { AgencyPackagePreview } from '../../interfaces/model/AgencyPackagePreview';
import { AgencyPackageService } from './services/agency-package.service';
import { AgencyPackageQueryParams } from '../../interfaces/params/AgencyPackageQueryParams';
import { AgencyPackageFilter } from '../../interfaces/filters/AgencyPackageFilter';
import { PaginationFilter } from '../../interfaces/filters/PaginationFilter';
import { AgencyPackageFilterComponent } from './ui/agency-package-filter/agency-package-filter.component';
import { AgencyPackageTabComponent } from '../home-tabs/ui/agency-package-tab/agency-package-tab.component';
import { AgencyPackageListComponent } from './ui/agency-package-list/agency-package-list.component';
import { NotFoundComponent } from '../../shared/not-found.component';
import { AgencyPackageSkeletonComponent } from './ui/agency-package-skeleton/agency-package-skeleton.component';

@Component({
  selector: 'smt-agency-package',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    TextComponent,
    AgencyPackageFilterComponent,
    AgencyPackageTabComponent,
    AgencyPackageListComponent,
    NotFoundComponent,
    AgencyPackageSkeletonComponent,
  ],
  templateUrl: './agency-package.component.html',
  styles: `
    ::ng-deep .p-accordion .p-accordion-header .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
  `,
})
export class AgencyPackageComponent {
  // Local variables
  private currentRoute = '/agency-packages';
  accordionItemIndex: number = -1; // Search accordion state
  sortFields = ['bla']; // TODO: fill

  // Status variables
  params$!: Observable<{
    searchParams: AgencyPackageQueryParams;
    paginationParams: PaginationFilter;
    filterParams: AgencyPackageFilter;
  }>;
  packages$!: Observable<QueryResult<PagedRes<AgencyPackagePreview>>>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private agencyPackageService = inject(AgencyPackageService);

  ngOnInit() {
    // Reactively listen to query param changes
    this.params$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractSearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractFilterParams(params),
      })),
    );
    this.packages$ = this.params$.pipe(
      switchMap(({ searchParams, filterParams, paginationParams }) =>
        this.fetchAgencyPackages(searchParams, filterParams, paginationParams),
      ),
      tap(() => {
        // Close the accordion after a new search
        this.accordionItemIndex = -1;
      }),
    );
  }

  handlePackageSearch(queryParams: AgencyPackageQueryParams) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
    });
  }

  handleFilter(filterParams: AgencyPackageFilter) {
    const currentParams = { ...this.route.snapshot.queryParams };

    // Explicitly handle undefined values by removing them
    Object.keys(filterParams).forEach((key) => {
      const typedKey = key as keyof AgencyPackageFilter;
      if (filterParams[typedKey] === undefined) {
        delete currentParams[typedKey];
      } else {
        currentParams[typedKey] = filterParams[typedKey];
      }
    });

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        ...currentParams,
      },
    });
  }

  handleSelectAgencyPackage(agencyPackage: AgencyPackagePreview) {
    this.router.navigate([this.currentRoute, agencyPackage.id]);
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

  /**
   * PRIVATE METHODS
   */

  private fetchAgencyPackages(
    searchParams: AgencyPackageQueryParams,
    filters?: AgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<AgencyPackagePreview>>> {
    return this.agencyPackageService.getAgencyPackages(
      searchParams.to,
      searchParams.toType,
      searchParams.startDate,
      searchParams.endDate,
      filters,
      pagination,
    );
  }

  private extractSearchParams(params: ParamMap): AgencyPackageQueryParams {
    return {
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
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

  private extractFilterParams(params: ParamMap): AgencyPackageFilter {
    return {
      name: params.get('name') ?? undefined,
      tags: params.getAll('tags') ?? [],
      minPrice: Number(params.get('minPrice')) || undefined,
      maxPrice: Number(params.get('maxPrice')) || undefined,
    };
  }
}
