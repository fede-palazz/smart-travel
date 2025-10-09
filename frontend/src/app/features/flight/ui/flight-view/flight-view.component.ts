import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { combineLatest, Observable, switchMap, tap } from 'rxjs';
import { Flight } from '../../../../interfaces/model/Flight';
import { PagedRes } from '../../../../interfaces/PagedRes';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { CardModule } from 'primeng/card';
import { TextComponent } from '../../../../shared/text.component';
import { FlightFiltersComponent } from '../flight-filters/flight-filters.component';
import { FlightListSkeletonComponent } from '../flight-list-skeleton/flight-list-skeleton.component';
import { FlightListComponent } from '../flight-list/flight-list.component';
import { Router, ActivatedRoute } from '@angular/router';
import { FlightService } from '../../services/flight.service';
import { FlightFilter } from '../../../../interfaces/filters/FlightFilter';
import { FlightQueryParams } from '../../../../interfaces/params/FlightQueryParams';
import { PaginationFilter } from '../../../../interfaces/filters/PaginationFilter';
import { PackageStep } from '../../../../interfaces/enums/PackageStep';
import { NotFoundComponent } from '../../../../shared/not-found.component';

@Component({
  selector: 'smt-flight-view',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AccordionModule,
    AvatarModule,
    FlightListComponent,
    FlightFiltersComponent,
    TextComponent,
    BreadcrumbModule,
    CardModule,
    AccordionModule,
    FlightListSkeletonComponent,
    NotFoundComponent,
  ],
  templateUrl: './flight-view.component.html',
  styles: `
    ::ng-deep .p-accordion .p-accordion-header .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
  `,
})
export class FlightViewComponent implements OnInit {
  // Local variables
  sortFields = ['bla']; // TODO: fill
  step = PackageStep; // To use step enum in template
  accordionItemIndex: number = -1; // Search accordion state

  // Status variables
  @Input({ required: true }) params$!: Observable<{
    searchParams: FlightQueryParams;
    paginationParams: PaginationFilter;
    filterParams: FlightFilter;
  }>;
  @Input({ required: true }) step$!: Observable<PackageStep>;
  flights$!: Observable<QueryResult<PagedRes<Flight>>>;

  // Events
  @Output() onSelectFlight = new EventEmitter<Flight>();

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private flightService = inject(FlightService);

  ngOnInit(): void {
    // Fetch flights every time query params or current step change
    this.flights$ = combineLatest([this.params$, this.step$]).pipe(
      switchMap(
        ([{ searchParams, filterParams, paginationParams }, currentStep]) =>
          this.fetchFlights(
            currentStep,
            searchParams,
            filterParams,
            paginationParams,
          ),
      ),
      tap(() => {
        // Close the accordion after a new search
        this.accordionItemIndex = -1;
      }),
    );
  }

  handleSelectFlight(flight: Flight) {
    this.onSelectFlight.emit(flight);
  }

  handleFilter(filterParams: FlightFilter): void {
    const currentParams = { ...this.route.snapshot.queryParams };

    // Explicitly handle undefined values by removing them
    Object.keys(filterParams).forEach((key) => {
      const typedKey = key as keyof FlightFilter;
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

  /**
   * PRIVATE METHODS
   */

  private fetchFlights(
    currentStep: PackageStep,
    searchParams: FlightQueryParams,
    filterParams?: FlightFilter,
    paginationParams?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<Flight>>> {
    if (currentStep === PackageStep.FLIGHT_DEPARTURE) {
      // Get departure flights
      return this.flightService.getFlights(
        searchParams.from,
        searchParams.fromType,
        searchParams.to,
        searchParams.toType,
        searchParams.startDate,
        filterParams,
        paginationParams,
      );
    }
    // Get return flights
    return this.flightService.getFlights(
      searchParams.to,
      searchParams.toType,
      searchParams.from,
      searchParams.fromType,
      searchParams.endDate!,
      filterParams,
      paginationParams,
    );
  }
}
