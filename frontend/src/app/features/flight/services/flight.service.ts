import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { FlightFilter } from '../../../interfaces/filters/FlightFilter';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { QueryResult } from '../../../interfaces/QueryResult';
import { PagedRes } from '../../../interfaces/PagedRes';
import { Flight } from '../../../interfaces/model/Flight';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  constructor(private apollo: Apollo) {}

  getFlights(
    from: string,
    fromType: string,
    to: string,
    toType: string,
    departureDate: string,
    filters?: FlightFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<Flight>>> {
    const fromQuery =
      fromType === 'city'
        ? 'fromCity'
        : fromType === 'region'
          ? 'fromRegion'
          : 'fromCountry';
    const toQuery =
      toType === 'city'
        ? 'toCity'
        : toType === 'region'
          ? 'toRegion'
          : 'toCountry';

    // Compose filter query
    let filterQuery = '';
    if (filters?.airline !== undefined) {
      filterQuery += `airline: "${filters.airline}", `;
    }
    if (filters?.minPrice !== undefined) {
      filterQuery += `minPrice: ${filters.minPrice}, `;
    }
    if (filters?.maxPrice !== undefined) {
      filterQuery += `maxPrice: ${filters.maxPrice}, `;
    }

    // Compose pagination query
    let paginationQuery = '';
    if (pagination) {
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
    }

    // Get user timezone
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;

    // Assemble query
    const query = gql`
      {
        getFlights(
          ${paginationQuery}
          timezone: "${timezone}"
          filters: {
            ${fromQuery}: "${from}",
            ${toQuery}: "${to}",
            ${filterQuery}
            departureDate: "${departureDate}"
          }
      ) {
          currentPage
          elementsInPage
          totalElements
          totalPages
          content {
            id
            code
            airline
            airlineLogo
            capacity
            departureTime
            arrivalTime
            from {
              destinationId
              airportName
              airportCode
              city
              region
              timezone
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
              airportCode
              airportName
              timezone
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
              currency
              value
            }
          }
        }
      }
    `;
    return this.apollo
      .watchQuery<{ getFlights: PagedRes<Flight> }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getFlights ?? null,
        })),
      );
  }

  getFlightDetails(id: string): Observable<QueryResult<Flight>> {
    const query = gql`
      {
        getFlightById(id: "${id}") {
          id
          code
          capacity
          airline
          airlineLogo
          departureTime
          arrivalTime
          price {
            value
            currency
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
        }
      }`;
    return this.apollo
      .watchQuery<{ getFlightById: Flight }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getFlightById ?? null,
        })),
      );
  }
}
