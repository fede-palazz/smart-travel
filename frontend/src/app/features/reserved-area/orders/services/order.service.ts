import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { OrderFilter } from '../../../../interfaces/filters/OrderFilter';
import { Order, OrderPreview } from '../../../../interfaces/orders/Order';
import { PagedRes } from '../../../../interfaces/PagedRes';
import { PaginationFilter } from '../../../../interfaces/filters/PaginationFilter';
import { map, Observable } from 'rxjs';
import { QueryResult } from '../../../../interfaces/QueryResult';

const GET_ORDERS_PREVIEW_QUERY = (
  paginationQuery: string = '',
  filterQuery: string = '',
) => gql`
      {
        getOrders(
          ${paginationQuery}
          filters: { 
              ${filterQuery}
          }
        ) {
          currentPage
          elementsInPage
          totalElements
          totalPages
          content {
            id
            createdAt
            type
            agencyPackageId
            customerInfo {
                userId
                email
                name
                surname
            }
            payment {
                status
                paidAt
                amount {
                    value
                    currency
                }
            }
          }
        }
      }
    `;

const GET_ORDER_QUERY = (id: string) => gql`
      {
        getOrderById(id: "${id}") {
            id
            createdAt
            type
            agencyPackageId
            customerInfo {
                userId
                email
                name
                surname
            }
            payment {
                status
                paidAt
                amount {
                    value
                    currency
                }
            }
            items {
                departureFlight {
                    flightId
                    code
                    quantity
                    airline
                    airlineLogo
                    departureTime
                    arrivalTime
                    from {
                        destinationId
                        city
                        region
                        timezone
                        airportCode
                        airportName
                        country {
                            code
                            name
                        }
                        coordinates {
                            lat
                            lng
                        }
                    }
                    to {
                        destinationId
                        city
                        region
                        timezone
                        airportCode
                        airportName
                        country {
                            code
                            name
                        }
                        coordinates {
                            lat
                            lng
                        }
                    }
                    price {
                        value
                        currency
                    }
                }
                returnFlight {
                    flightId
                    code
                    quantity
                    airline
                    airlineLogo
                    departureTime
                    arrivalTime
                    from {
                        destinationId
                        city
                        region
                        timezone
                        airportCode
                        airportName
                        country {
                            code
                            name
                        }
                        coordinates {
                            lat
                            lng
                        }
                    }
                    to {
                        destinationId
                        city
                        region
                        timezone
                        airportCode
                        airportName
                        country {
                            code
                            name
                        }
                        coordinates {
                            lat
                            lng
                        }
                    }
                    price {
                        value
                        currency
                    }
                }
                accommodation {
                    accommodationId
                    name
                    type
                    mainPicture
                    startDate
                    endDate
                    rooms {
                        name
                        type
                        capacity
                        quantity
                        amenities
                        bedTypes
                        pictures
                        pricePerNight {
                            value
                            currency
                        }
                    }
                }
                activities {
                    activityId
                    name
                    type
                    mainPicture
                    date
                    startTime
                    endTime
                    quantity
                    price {
                        value
                        currency
                    }
                }
			      }
        }
      }
    `;

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  constructor(private apollo: Apollo) {}

  getOrders(
    filters?: OrderFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<OrderPreview>>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    return this.apollo
      .watchQuery<{
        getOrders: PagedRes<OrderPreview>;
      }>({
        query: GET_ORDERS_PREVIEW_QUERY(paginationQuery, filterQuery),
        fetchPolicy: 'network-only',
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getOrders ?? null,
        })),
      );
  }

  getOrderById(id: string): Observable<QueryResult<Order>> {
    return this.apollo
      .watchQuery<{
        getOrderById: Order;
      }>({
        query: GET_ORDER_QUERY(id),
        fetchPolicy: 'network-only',
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getOrderById ?? null,
        })),
      );
  }

  /**
   * PRIVATE METHODS
   */

  private getPaginationQuery(pagination: PaginationFilter): string {
    // Compose pagination query
    let paginationQuery = '';
    if (pagination.page !== undefined) {
      paginationQuery += `page: ${pagination.page}, `;
    }
    if (pagination.size !== undefined) {
      paginationQuery += `size: ${pagination.size}, `;
    }
    if (pagination.sort !== undefined) {
      paginationQuery += `sort: "${pagination.sort}", `;
    }
    if (pagination.order !== undefined) {
      paginationQuery += `order: "${pagination.order}", `;
    }

    return paginationQuery;
  }

  private getFilterQuery(filters: OrderFilter): string {
    // Compose filter query
    let filterQuery = '';

    if (filters.customerId !== undefined) {
      filterQuery += `customerId: "${filters.customerId}", `;
    }
    if (filters.orderId !== undefined) {
      filterQuery += `orderId: "${filters.orderId}", `;
    }
    if (filters.customerName !== undefined) {
      filterQuery += `customerName: "${filters.customerName}", `;
    }
    if (filters.status !== undefined) {
      filterQuery += `status: "${filters.status}", `;
    }
    if (filters.type !== undefined) {
      filterQuery += `type: "${filters.type}", `;
    }
    if (filters.minAmount !== undefined) {
      filterQuery += `minAmount: ${filters.minAmount}, `;
    }
    if (filters.maxAmount !== undefined) {
      filterQuery += `maxAmount: ${filters.maxAmount}, `;
    }

    return filterQuery;
  }
}
