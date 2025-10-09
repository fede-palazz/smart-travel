import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { OrderItems } from '../../../interfaces/orders/Order';
import { FlightOrder } from '../../../interfaces/orders/FlightOrder';
import { AccommodationOrder } from '../../../interfaces/orders/AccommodationOrder';
import { Apollo, gql } from 'apollo-angular';
import {
  PartialAccommodationOrder,
  PartialActivityOrder,
  PartialFlightOrder,
  PartialOrderReq,
} from '../../../interfaces/orders/OrderReq';
import { ActivityOrder } from '../../../interfaces/orders/ActivityOrder';
import { QueryResult } from '../../../interfaces/QueryResult';
import { OrderRes } from '../../../interfaces/orders/OrderRes';

@Injectable({
  providedIn: 'root',
})
export class CheckoutService {
  private returnUrl: string = '';
  private orderSubject = new BehaviorSubject<OrderItems>({});
  order$ = this.orderSubject.asObservable();

  // Injectables
  private apollo = inject(Apollo);

  constructor() {
    // this.order$.subscribe((o) => console.log(o));
  }

  getOrder(): OrderItems {
    return this.orderSubject.getValue();
  }

  setOrder(order: OrderItems) {
    this.orderSubject.next(order);
  }

  isOrderPresent(): boolean {
    const order = this.getOrder();
    return !!order && Object.keys(order).length > 0;
  }

  isOrderComplete(options: string[]): boolean {
    const order = this.getOrder();

    if (!this.isOrderPresent()) return false;
    if (
      options.includes('flight') &&
      (!order.departureFlight || !order.returnFlight)
    )
      return false;
    if (options.includes('stay') && !order.accommodation) return false;
    if (
      options.includes('activity') &&
      (!order.activities || order.activities.length === 0)
    )
      return false;
    return true;
  }

  isEmpty(): boolean {
    const order = this.getOrder();
    return (
      order.departureFlight === undefined &&
      order.returnFlight === undefined &&
      order.accommodation === undefined &&
      (order.activities?.length ?? 0) === 0
    );
  }

  clearOrder() {
    if (!this.isEmpty()) this.orderSubject.next({});
    this.returnUrl = '';
  }

  setReturnUrl(url: string) {
    this.returnUrl = url;
  }

  getReturnUrl(): string {
    return this.returnUrl;
  }

  setDepartureFlight(flight: FlightOrder) {
    this.orderSubject.next({
      ...this.getOrder(),
      departureFlight: flight,
    });
  }

  setReturnFlight(flight: FlightOrder) {
    this.orderSubject.next({
      ...this.getOrder(),
      returnFlight: flight,
    });
  }

  setAccommodation(accommodation: AccommodationOrder) {
    this.orderSubject.next({
      ...this.getOrder(),
      accommodation: accommodation,
    });
  }

  addActivity(activity: ActivityOrder) {
    let activities: ActivityOrder[] = this.getOrder().activities ?? [];

    // If the activity is already present, filter it out
    activities = activities.filter(
      (act) => act.activityId !== activity.activityId,
    );
    activities.push(activity);

    this.orderSubject.next({
      ...this.getOrder(),
      activities: activities,
    });
  }

  removeActivity(id: string) {
    const activities: ActivityOrder[] = this.getOrder().activities ?? [];

    this.orderSubject.next({
      ...this.getOrder(),
      activities: activities.filter((activity) => activity.activityId !== id),
    });
  }

  /**
   * API CALLS
   */

  performCheckout(
    departureFlight?: PartialFlightOrder,
    returnFlight?: PartialFlightOrder,
    accommodation?: PartialAccommodationOrder,
    activities?: PartialActivityOrder[],
  ): Observable<QueryResult<OrderRes>> {
    const CHECKOUT_MUTATION = gql`
      mutation createOrder($partialOrderReq: PartialOrderReq!) {
        createOrder(partialOrderReq: $partialOrderReq) {
          redirectURL
        }
      }
    `;
    // Create order request
    const orderReq: PartialOrderReq = {};

    if (departureFlight) {
      orderReq.departureFlight = departureFlight;

      if (returnFlight) {
        orderReq.returnFlight = returnFlight;
      }
    }
    if (accommodation) {
      orderReq.accommodation = accommodation;
    }
    if (activities && activities.length > 0) {
      orderReq.activities = activities;
    }

    return this.apollo
      .mutate<{ createOrder: OrderRes }>({
        mutation: CHECKOUT_MUTATION,
        variables: {
          partialOrderReq: orderReq,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.createOrder ?? null,
        })),
      );
  }
}
