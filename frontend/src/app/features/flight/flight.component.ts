import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { Flight } from '../../interfaces/model/Flight';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { FlightTabComponent } from '../home-tabs/ui/flight-tab/flight-tab.component';
import { CardModule } from 'primeng/card';
import { FlightFilter } from '../../interfaces/filters/FlightFilter';
import { PaginationFilter } from '../../interfaces/filters/PaginationFilter';
import { FlightQueryParams } from '../../interfaces/params/FlightQueryParams';
import { CheckoutService } from '../checkout/services/checkout.service';
import { RouterUtils } from '../../utils/RouterUtils';
import { FlightViewComponent } from './ui/flight-view/flight-view.component';
import { PackageStep } from '../../interfaces/enums/PackageStep';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'smt-flight',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AccordionModule,
    AvatarModule,
    BreadcrumbModule,
    FlightTabComponent,
    CardModule,
    AccordionModule,
    FlightViewComponent,
  ],
  templateUrl: './flight.component.html',
  styles: ``,
})
export class FlightComponent implements OnInit {
  // Local variables
  items!: MenuItem[]; // Breadcrumb items
  step = PackageStep; // To use step in template
  flightParams$!: Observable<{
    searchParams: FlightQueryParams;
    paginationParams: PaginationFilter;
    filterParams: FlightFilter;
  }>;

  // Status variables
  stepSubject = new BehaviorSubject<PackageStep>(PackageStep.FLIGHT_DEPARTURE);
  step$ = this.stepSubject.asObservable();

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
    ];
  }

  ngOnInit() {
    // Extract query params on route change
    this.flightParams$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractSearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractFilterParams(params),
      })),
    );
    // Set initial step
    const step = this.route.snapshot.queryParamMap.get('step');
    const departureFlight = this.checkoutService.getOrder().departureFlight;

    if (step && departureFlight && step === PackageStep.FLIGHT_RETURN)
      this.stepSubject.next(PackageStep.FLIGHT_RETURN);
  }

  handleSelectFlight(flight: Flight) {
    const params = this.route.snapshot.queryParamMap;
    if (!params) return;
    const type = params.get('type')!;
    const quantity = Number(params.get('quantity')) || 1;

    if (this.stepSubject.getValue() === PackageStep.FLIGHT_DEPARTURE)
      this.handleSelectDepartureFlight(flight, type, quantity);
    else this.handleSelectReturnFlight(flight, quantity);
  }

  handleFlightSearch(queryParams: FlightQueryParams) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
    });
  }

  handleChangeStep(step: PackageStep) {
    const currentStep = this.stepSubject.getValue();

    if (step === PackageStep.HOME) {
      this.router.navigate(['/home']);
      return;
    }
    if (step === currentStep) return;
    this.stepSubject.next(step);
  }

  /**
   * PRIVATE METHODS
   */

  private handleSelectDepartureFlight(
    flight: Flight,
    type: string,
    quantity: number,
  ) {
    const { id, capacity, ...orderFlight } = flight;

    // Move to next step
    if (type === 'one-way') {
      // Move to checkout page
      this.checkoutService.setDepartureFlight({
        flightId: flight.id,
        quantity: quantity,
        ...orderFlight,
      });
      this.checkoutService.setReturnUrl(
        RouterUtils.getCleanedUrl(this.router, this.router.url),
      );
      this.router.navigate(['/checkout']);
    } else {
      // Move to return flight step
      this.stepSubject.next(PackageStep.FLIGHT_RETURN);
      // Save departure flight selection
      this.checkoutService.setDepartureFlight({
        flightId: flight.id,
        quantity: quantity,
        ...orderFlight,
      });
    }
  }

  private handleSelectReturnFlight(flight: Flight, quantity: number) {
    // Move to checkout page
    this.checkoutService.setReturnFlight({
      ...flight,
      flightId: flight.id,
      quantity: quantity,
    });
    this.checkoutService.setReturnUrl(
      RouterUtils.getCleanedUrl(this.router, this.router.url),
    );
    this.router.navigate(['/checkout']);
  }

  private extractSearchParams(params: ParamMap): FlightQueryParams {
    return {
      type: params.get('type') ?? '',
      from: params.get('from') ?? '',
      fromType: params.get('fromType') ?? '',
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? undefined,
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

  private extractFilterParams(params: ParamMap): FlightFilter {
    return {
      airline: params.get('airline') ?? undefined,
      minPrice: Number(params.get('minPrice')) || undefined,
      maxPrice: Number(params.get('maxPrice')) || undefined,
    };
  }
}
