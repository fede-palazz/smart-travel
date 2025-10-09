import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { MenuItem, MessageService } from 'primeng/api';
import {
  Observable,
  BehaviorSubject,
  distinctUntilChanged,
  map,
  tap,
} from 'rxjs';
import { PackageStep } from '../../../interfaces/enums/PackageStep';
import { AccommodationFilter } from '../../../interfaces/filters/AccommodationFilter';
import { ActivityFilter } from '../../../interfaces/filters/ActivityFilter';
import { FlightFilter } from '../../../interfaces/filters/FlightFilter';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { AccommodationPreview } from '../../../interfaces/model/AccommodationPreview';
import { Activity } from '../../../interfaces/model/Activity';
import { Flight } from '../../../interfaces/model/Flight';
import { AccommodationOrder } from '../../../interfaces/orders/AccommodationOrder';
import { ActivityOrder } from '../../../interfaces/orders/ActivityOrder';
import { AccommodationQueryParams } from '../../../interfaces/params/AccommodationQueryParams';
import { ActivityQueryParams } from '../../../interfaces/params/ActivityQueryParams';
import { FlightQueryParams } from '../../../interfaces/params/FlightQueryParams';
import { RouterUtils } from '../../../utils/RouterUtils';
import { CheckoutService } from '../../checkout/services/checkout.service';
import { AgencyPackageNewQueryParams } from '../../../interfaces/params/AgencyPackageNewQueryParams';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { FlightViewComponent } from '../../flight/ui/flight-view/flight-view.component';
import { AccommodationViewComponent } from '../../accommodation/ui/accommodation-view/accommodation-view.component';
import { AccommodationDetailsViewComponent } from '../../accommodation-details/ui/accommodation-details-view/accommodation-details-view.component';
import { ActivityViewComponent } from '../../activity/ui/activity-view/activity-view.component';
import { ActivityDetailsViewComponent } from '../../activity-details/ui/activity-details-view/activity-details-view.component';
import { AgencyPackagesNewTabComponent } from './ui/agency-packages-new-tab/agency-packages-new-tab.component';
import { AgencyPackagesConfirmationComponent } from './ui/agency-packages-confirmation/agency-packages-confirmation.component';
import { OrderItems } from '../../../interfaces/orders/Order';
import { CheckoutConfirmationComponent } from '../../checkout/ui/checkout-confirmation/checkout-confirmation.component';
import { AuthService } from '../../../services/auth.service';
import { PartialAgencyPackage } from '../../../interfaces/model/AgencyPackage';
import { AgencyPackageReq } from '../../../interfaces/orders/AgencyPackageReq';
import { ToastModule } from 'primeng/toast';
import { AgencyPackagesService } from '../agency-packages/services/agency-packages.service';

@Component({
  selector: 'smt-agency-packages-new',
  standalone: true,
  imports: [
    CommonModule,
    BreadcrumbModule,
    FlightViewComponent,
    AccommodationViewComponent,
    AccommodationDetailsViewComponent,
    ActivityViewComponent,
    ActivityDetailsViewComponent,
    AgencyPackagesNewTabComponent,
    AgencyPackagesConfirmationComponent,
    CheckoutConfirmationComponent,
    ToastModule,
  ],
  templateUrl: './agency-packages-new.component.html',
  styles: ``,
})
export class AgencyPackagesNewComponent {
  // Local variables
  private destroyRef = inject(DestroyRef);
  options = ['flight', 'stay', 'activity'];
  items!: MenuItem[]; // Breadcrumb items
  step = PackageStep; // To use step enum in template
  initialParams!: AgencyPackageNewQueryParams; // Search query params
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
  order$!: Observable<OrderItems>;
  isSaving = false; // Loading spinner

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private checkoutService = inject(CheckoutService);
  private authService = inject(AuthService);
  private messageService = inject(MessageService);
  private agencyPackagesService = inject(AgencyPackagesService);

  constructor() {
    // Initialize breadcrumb items
    this.items = [
      { label: 'Dashboard', icon: 'dashboard', index: PackageStep.HOME },
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
      {
        label: 'Review choices',
        icon: 'grading',
        index: PackageStep.CHECKOUT,
      },
      {
        label: 'Confirm',
        icon: 'approval',
        index: PackageStep.CONFIRM,
      },
    ];
  }

  ngOnInit(): void {
    // Set order observable
    this.order$ = this.checkoutService.order$;
    // Set initial parameters
    this.initialParams = this.extractSearchParams(
      this.route.snapshot.queryParamMap,
    );
    // Set initial step
    const step = this.route.snapshot.queryParamMap.get('step') as PackageStep;
    if (
      this.checkoutService.isOrderComplete(this.options) &&
      [
        'flight_departure',
        'flight_return',
        'accommodation',
        'activity',
      ].includes(step)
    ) {
      this.stepSubject.next(step);
    } else {
      this.stepSubject.next(this.getFirstStep());
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
        // Check if step is HOME
        if (currentStep === PackageStep.HOME) {
          this.router.navigate(['/dashboard/agency-packages']);
          return;
        }
        // Reset query params to the initial ones
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

        // Flight option
        // Add FLIGHT_DEPARTURE step
        items.push(...this.items.slice(1, 2));
        if (step !== PackageStep.FLIGHT_DEPARTURE)
          // Add FLIGHT_RETURN step
          items.push(...this.items.slice(2, 3));

        // Accommodation option
        if (
          step !== PackageStep.FLIGHT_DEPARTURE &&
          step !== PackageStep.FLIGHT_RETURN
        ) {
          // Add ACCOMMODATION step
          items.push(...this.items.slice(3, 4));
        }

        // Activity option
        if (
          step !== PackageStep.FLIGHT_DEPARTURE &&
          step !== PackageStep.FLIGHT_RETURN &&
          step !== PackageStep.ACCOMMODATION &&
          step !== PackageStep.ACCOMMODATION_DETAILS
        ) {
          // Add ACTIVITY step
          items.push(...this.items.slice(4, 5));
        }

        // Checkout option
        if (
          step !== PackageStep.FLIGHT_DEPARTURE &&
          step !== PackageStep.FLIGHT_RETURN &&
          step !== PackageStep.ACCOMMODATION &&
          step !== PackageStep.ACCOMMODATION_DETAILS &&
          step !== PackageStep.ACTIVITY &&
          step !== PackageStep.ACTIVITY_DETAILS
        ) {
          // Add REVIEW step
          items.push(...this.items.slice(5, 6));
        }

        // Confirm option
        if (
          step !== PackageStep.FLIGHT_DEPARTURE &&
          step !== PackageStep.FLIGHT_RETURN &&
          step !== PackageStep.ACCOMMODATION &&
          step !== PackageStep.ACCOMMODATION_DETAILS &&
          step !== PackageStep.ACTIVITY &&
          step !== PackageStep.ACTIVITY_DETAILS &&
          step !== PackageStep.CHECKOUT
        ) {
          // Add CONFIRM step
          items.push(...this.items.slice(6, 7));
        }

        return items;
      }),
    );
  }

  handleCustomPackageSearch(queryParams: AgencyPackageNewQueryParams) {
    // Update initial params
    this.initialParams = queryParams;
    // Clear the checkout store
    this.checkoutService.clearOrder();
    this.accommodationId = undefined;
    this.activityId = undefined;
    // Update current step
    this.stepSubject.next(this.getFirstStep());
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

  handleDeleteActivity(id: string) {
    this.checkoutService.removeActivity(id);
  }

  handleSavePackage(partialReq: PartialAgencyPackage) {
    const order = this.checkoutService.getOrder();
    const user = this.authService.getUser();

    if (!order || !user) return;

    const destination = order.departureFlight!.to;

    const packageReq: AgencyPackageReq = {
      ...partialReq,
      pictures: [partialReq.mainPicture],
      startDate: this.initialParams.startDate,
      endDate: this.initialParams.endDate,
      destination: {
        destinationId: destination.destinationId,
        city: destination.city,
        region: destination.region,
        country: destination.country,
        coordinates: destination.coordinates,
        timezone: destination.timezone,
      },
      departureFlight: order.departureFlight!,
      returnFlight: order.returnFlight!,
      accommodation: order.accommodation!,
      activities: order.activities!,
      agentInfo: {
        userId: user.id,
        email: user.email,
        name: user.firstname,
        surname: user.lastname,
      },
      quantity: this.initialParams.quantity,
    };

    this.agencyPackagesService
      .addAgencyPackage(packageReq)
      .pipe(
        tap(({ loading }) => {
          this.isSaving = loading;
        }),
      )
      .subscribe(({ data, error }) => {
        if (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error while saving package',
            detail: error,
          });
          this.isSaving = false;
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: `Package added successfully`,
            detail: 'You are being redirected...',
          });
          setTimeout(() => {
            this.router.navigate(['/dashboard/agency-packages']);
          }, 1000);
        }
      });
  }

  /**
   * PRIVATE METHODS
   */

  private moveToNextStep(): void {
    const currentStep = this.stepSubject.getValue();
    const options = this.options;
    const nextStep = this.getNextStep(currentStep, options);
    this.stepSubject.next(nextStep);
  }

  private getFirstStep(): PackageStep {
    return PackageStep.FLIGHT_DEPARTURE;
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

      case PackageStep.CHECKOUT:
        return PackageStep.CONFIRM;

      default:
        return PackageStep.HOME;
    }
  }

  private extractSearchParams(params: ParamMap): AgencyPackageNewQueryParams {
    return {
      from: params.get('from') ?? '',
      fromType: params.get('fromType') ?? '',
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
