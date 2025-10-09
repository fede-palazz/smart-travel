import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { AccommodationFilter } from '../../../interfaces/filters/AccommodationFilter';
import { PagedRes } from '../../../interfaces/PagedRes';
import { AccommodationPreview } from '../../../interfaces/model/AccommodationPreview';
import { map, Observable } from 'rxjs';
import { QueryResult } from '../../../interfaces/QueryResult';

@Injectable({
  providedIn: 'root',
})
export class AccommodationService {
  private apollo = inject(Apollo);

  constructor() {}

  getAccommodations(
    to: string,
    toType: string,
    startDate: string,
    endDate: string,
    guests: number,
    filters?: AccommodationFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<AccommodationPreview>>> {
    const toQuery =
      toType === 'city' ? 'city' : toType === 'region' ? 'region' : 'country';

    // Compose filter query
    let filterQuery = '';
    if (filters?.name !== undefined) {
      filterQuery += `name: "${filters.name}", `;
    }
    if (filters?.types && filters.types.length > 0) {
      const typesString = `[${filters.types
        .map((type) => `"${type}"`)
        .join(', ')}]`;
      filterQuery += `types: ${typesString}, `;
    }
    if (filters?.services && filters.services.length > 0) {
      const servicesString = `[${filters.services
        .map((service) => `"${service}"`)
        .join(', ')}]`;
      filterQuery += `services: ${servicesString}, `;
    }
    if (filters?.minDistanceToCenterKm !== undefined) {
      filterQuery += `minDistanceToCenterKm: ${filters.minDistanceToCenterKm}, `;
    }
    if (filters?.maxDistanceToCenterKm !== undefined) {
      filterQuery += `maxDistanceToCenterKm: ${filters.maxDistanceToCenterKm}, `;
    }
    if (filters?.minPricePerNight !== undefined) {
      filterQuery += `minPricePerNight: ${filters.minPricePerNight}, `;
    }
    if (filters?.maxPricePerNight !== undefined) {
      filterQuery += `maxPricePerNight: ${filters.maxPricePerNight}, `;
    }
    if (filters?.minRating !== undefined) {
      filterQuery += `minRating: ${filters.minRating}, `;
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
          getAccommodations(
            ${paginationQuery}
            timezone: "${timezone}",
            filters: { 
                ${toQuery}: "${to}",
                ${filterQuery}
                startDate: "${startDate}",
                endDate: "${endDate}",
                guests: ${guests}
            }
          ) {
            currentPage
            elementsInPage
            totalElements
            totalPages
            content {
              id
              name
              type
              address
              services
              coordinates {
                lat
                lng
              }
              distanceToCenterKm
              mainPicture
              reviewsSummary {
                avgRating
                totalCount
              }
              pricePerNight
              destination {
                city
                region
                timezone
                country {
                  name
                }
                coordinates {
                  lat
                  lng
                }
              }
            }
          }
        }
      `;
    return this.apollo
      .watchQuery<{ getAccommodations: PagedRes<AccommodationPreview> }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getAccommodations ?? null,
        })),
      );
  }
}
