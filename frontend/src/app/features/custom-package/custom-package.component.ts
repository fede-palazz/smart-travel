import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { BehaviorSubject, distinctUntilChanged, map, Observable } from 'rxjs';
import { CheckoutService } from '../checkout/services/checkout.service';
import { CustomPackageQueryParams } from '../../interfaces/params/CustomPackageQueryParams';
import { CustomPackageTabComponent } from '../home-tabs/ui/custom-package-tab/custom-package-tab.component';
import { FlightViewComponent } from '../flight/ui/flight-view/flight-view.component';
import { FlightQueryParams } from '../../interfaces/params/FlightQueryParams';
import { FlightFilter } from '../../interfaces/filters/FlightFilter';
import { PaginationFilter } from '../../interfaces/filters/PaginationFilter';
import { Flight } from '../../interfaces/model/Flight';
import { RouterUtils } from '../../utils/RouterUtils';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AccommodationViewComponent } from '../accommodation/ui/accommodation-view/accommodation-view.component';
import { AccommodationFilter } from '../../interfaces/filters/AccommodationFilter';
import { AccommodationQueryParams } from '../../interfaces/params/AccommodationQueryParams';
import { AccommodationPreview } from '../../interfaces/model/AccommodationPreview';
import { AccommodationDetailsViewComponent } from '../accommodation-details/ui/accommodation-details-view/accommodation-details-view.component';
import { AccommodationOrder } from '../../interfaces/orders/AccommodationOrder';
import { ActivityViewComponent } from '../activity/ui/activity-view/activity-view.component';
import { Activity } from '../../interfaces/model/Activity';
import { ActivityFilter } from '../../interfaces/filters/ActivityFilter';
import { ActivityQueryParams } from '../../interfaces/params/ActivityQueryParams';
import { ActivityDetailsViewComponent } from '../activity-details/ui/activity-details-view/activity-details-view.component';
import { ActivityOrder } from '../../interfaces/orders/ActivityOrder';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { MenuItem } from 'primeng/api';
import { PackageStep } from '../../interfaces/enums/PackageStep';

@Component({
  selector: 'smt-custom-package',
  standalone: true,
  imports: [
    CommonModule,
    CustomPackageTabComponent,
    FlightViewComponent,
    AccommodationViewComponent,
    AccommodationDetailsViewComponent,
    ActivityViewComponent,
    ActivityDetailsViewComponent,
    BreadcrumbModule,
  ],
  templateUrl: './custom-package.component.html',
  styles: ``,
})
export class CustomPackageComponent implements OnInit {
  // Local variables
  private destroyRef = inject(DestroyRef);
  items!: MenuItem[]; // Breadcrumb items
  step = PackageStep; // To use step enum in template
  initialParams!: CustomPackageQueryParams; // Search query params
  flightParams$!: Observable<{
    searchParams: FlightQueryParams;
    paginationParams: PaginationFilter;
    filterParams: FlightFilter;
  }>;
  accommodationParams$!: Observable<{
    searchParams: AccommodationQueryParams;
    paginationParams: PaginationFilter;
    filterParams: AccommodationFilter;
  }>;
  activityParams$!: Observable<{
    searchParams: ActivityQueryParams;
    paginationParams: PaginationFilter;
    filterParams: ActivityFilter;
  }>;

  // Status variables
  stepSubject = new BehaviorSubject<PackageStep>(PackageStep.HOME);
  step$ = this.stepSubject.asObservable().pipe(distinctUntilChanged());
  items$!: Observable<MenuItem[]>;
  accommodationId?: string;
  activityId?: string;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private checkoutService = inject(CheckoutService);

  constructor() {
    // Initialize breadcrumb items
    this.items = [
      { label: 'Home', icon: 'home', index: PackageStep.HOME },
      {
        label: 'Departure flight',
        icon: 'flight_takeoff',
        index: PackageStep.FLIGHT_DEPARTURE,
      },
      {
        label: 'Return flight',
        icon: 'flight_land',
        index: PackageStep.FLIGHT_RETURN,
      },
      {
        label: 'Stay',
        icon: 'apartment',
        index: PackageStep.ACCOMMODATION,
      },
      {
        label: 'Activity',
        icon: 'local_activity',
        index: PackageStep.ACTIVITY,
      },
    ];
  }

  ngOnInit(): void {
    // Set initial parameters
    this.initialParams = this.extractSearchParams(
      this.route.snapshot.queryParamMap,
    );
    // Set initial step
    const step = this.route.snapshot.queryParamMap.get('step') as PackageStep;
    console.log(step);
    if (
      this.checkoutService.isOrderComplete(
        this.initialParams.options.filter((o) => o !== 'activity'),
      ) &&
      [
        'flight_departure',
        'flight_return',
        'accommodation',
        'activity',
      ].includes(step)
    ) {
      this.stepSubject.next(step);
    } else {
      this.stepSubject.next(this.getFirstStep(this.initialParams.options));
    }

    // Extract query params on route change
    this.flightParams$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractFlightSearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractFlightFilterParams(params),
      })),
    );
    this.accommodationParams$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractAccommodationSearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractAccommodationFilterParams(params),
      })),
    );
    this.activityParams$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractActivitySearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractActivityFilterParams(params),
      })),
    );

    // Clear query params on step change
    this.step$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((currentStep) => {
        // Check if current step is the checkout one
        if (currentStep === PackageStep.CHECKOUT) {
          this.router.navigate(['/checkout']);
          return;
        }
        // Otherwise reset query params to the initial ones
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {
            ...this.initialParams,
          },
        });
      });
    // Update breadcrumb items on step change
    this.items$ = this.step$.pipe(
      map((step) => {
        let items = this.items.slice(0, 1); // Initialize with HOME step
        const options = this.initialParams.options;
        // Flight option
        if (options.includes('flight')) {
          // Add FLIGHT_DEPARTURE step
          items.push(...this.items.slice(1, 2));
          if (step !== PackageStep.FLIGHT_DEPARTURE)
            // Add FLIGHT_RETURN step
            items.push(...this.items.slice(2, 3));
        }
        // Accommodation option
        if (options.includes('stay')) {
          if (
            step !== PackageStep.FLIGHT_DEPARTURE &&
            step !== PackageStep.FLIGHT_RETURN
          ) {
            // Add ACCOMMODATION step
            items.push(...this.items.slice(3, 4));
          }
        }
        // Activity option
        if (options.includes('activity')) {
          if (
            step !== PackageStep.FLIGHT_DEPARTURE &&
            step !== PackageStep.FLIGHT_RETURN &&
            step !== PackageStep.ACCOMMODATION &&
            step !== PackageStep.ACCOMMODATION_DETAILS
          ) {
            // Add ACTIVITY step
            items.push(...this.items.slice(4, 5));
          }
        }
        return items;
      }),
    );
  }

  handleCustomPackageSearch(queryParams: CustomPackageQueryParams) {
    // Update initial params
    this.initialParams = queryParams;
    // Clear the checkout store
    this.checkoutService.clearOrder();
    this.accommodationId = undefined;
    this.activityId = undefined;
    // Update current step
    this.stepSubject.next(this.getFirstStep(queryParams.options));
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
    });
  }

  handleSelectFlight(flight: Flight) {
    const currentStep = this.stepSubject.getValue();
    const quantity = this.initialParams.quantity;
    const { id, capacity, ...orderFlight } = flight;

    if (currentStep === PackageStep.FLIGHT_DEPARTURE) {
      // Save departure flight
      this.checkoutService.setDepartureFlight({
        flightId: flight.id,
        quantity: quantity,
        ...orderFlight,
      });
    } else {
      // Save return flight
      this.checkoutService.setReturnFlight({
        flightId: flight.id,
        quantity: quantity,
        ...orderFlight,
      });
    }
    // Save return URL
    this.checkoutService.setReturnUrl(
      RouterUtils.getCleanedUrl(this.router, this.router.url),
    );
    // Move to next step
    this.moveToNextStep();
  }

  handleSelectAccommodation(accommodation: AccommodationPreview) {
    this.accommodationId = accommodation.id;
    this.moveToNextStep();
  }

  handleSelectAccommodationDetails(accommodationOrder: AccommodationOrder) {
    // Save accommodation details
    this.checkoutService.setAccommodation(accommodationOrder);
    this.checkoutService.setReturnUrl(
      RouterUtils.getCleanedUrl(this.router, this.router.url),
    );
    // Move to next step
    this.moveToNextStep();
  }

  handleSelectActivity(activity: Activity) {
    this.activityId = activity.id;
    this.checkoutService.setReturnUrl(
      RouterUtils.getCleanedUrl(this.router, this.router.url),
    );
    this.moveToNextStep();
  }

  handleSelectActivityDetails(activityOrder: ActivityOrder) {
    // Save activity details
    this.checkoutService.addActivity(activityOrder);
    // Move to next step
    this.moveToNextStep();
  }

  handleChangeStep(step: PackageStep) {
    this.stepSubject.next(step);
  }

  /**
   * PRIVATE METHODS
   */

  private moveToNextStep(): void {
    const currentStep = this.stepSubject.getValue();
    const options = this.initialParams.options;
    const nextStep = this.getNextStep(currentStep, options);
    console.log(nextStep);
    this.stepSubject.next(nextStep);
  }

  private getFirstStep(options: string[]): PackageStep {
    if (options.includes('flight')) return PackageStep.FLIGHT_DEPARTURE;
    if (options.includes('stay')) return PackageStep.ACCOMMODATION;
    return PackageStep.ACTIVITY;
  }

  private getNextStep(currentStep: PackageStep, options: string[]) {
    switch (currentStep) {
      case PackageStep.FLIGHT_DEPARTURE:
        if (this.checkoutService.isOrderComplete(options))
          return PackageStep.CHECKOUT;
        return PackageStep.FLIGHT_RETURN;

      case PackageStep.FLIGHT_RETURN:
        if (this.checkoutService.isOrderComplete(options))
          return PackageStep.CHECKOUT;
        return options.includes('stay')
          ? PackageStep.ACCOMMODATION
          : PackageStep.ACTIVITY;

      case PackageStep.ACCOMMODATION:
        return PackageStep.ACCOMMODATION_DETAILS;

      case PackageStep.ACCOMMODATION_DETAILS:
        if (this.checkoutService.isOrderComplete(options))
          return PackageStep.CHECKOUT;
        return options.includes('activity')
          ? PackageStep.ACTIVITY
          : PackageStep.CHECKOUT;

      case PackageStep.ACTIVITY:
        return PackageStep.ACTIVITY_DETAILS;

      case PackageStep.ACTIVITY_DETAILS:
        return PackageStep.CHECKOUT;

      default:
        return PackageStep.HOME;
    }
  }

  private extractSearchParams(params: ParamMap): CustomPackageQueryParams {
    return {
      from: params.get('from') ?? '',
      fromType: params.get('fromType') ?? '',
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
      quantity: Number(params.get('quantity')) || 1,
      options: params.getAll('options') ?? [],
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

  // Flight params

  private extractFlightSearchParams(params: ParamMap): FlightQueryParams {
    return {
      type: 'return',
      from: params.get('from') ?? '',
      fromType: params.get('fromType') ?? '',
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
      quantity: Number(params.get('quantity')) || 1,
    };
  }

  private extractFlightFilterParams(params: ParamMap): FlightFilter {
    return {
      airline: params.get('airline') ?? undefined,
      minPrice: Number(params.get('minPrice')) || undefined,
      maxPrice: Number(params.get('maxPrice')) || undefined,
    };
  }

  // Accommodation params

  private extractAccommodationSearchParams(
    params: ParamMap,
  ): AccommodationQueryParams {
    return {
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
      quantity: Number(params.get('quantity')) || 1,
    };
  }

  private extractAccommodationFilterParams(
    params: ParamMap,
  ): AccommodationFilter {
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

  // Activity params

  private extractActivitySearchParams(params: ParamMap): ActivityQueryParams {
    return {
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
    };
  }

  private extractActivityFilterParams(params: ParamMap): ActivityFilter {
    return {
      name: params.get('name') ?? undefined,
      types: params.getAll('types') ?? [],
      tags: params.getAll('tags') ?? [],
      languages: params.getAll('languages') ?? [],
      minPrice: Number(params.get('minPrice')) || undefined,
      maxPrice: Number(params.get('maxPrice')) || undefined,
      minRating: Number(params.get('minRating')) || undefined,
    };
  }
}
