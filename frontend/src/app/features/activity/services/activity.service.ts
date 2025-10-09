import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { ActivityFilter } from '../../../interfaces/filters/ActivityFilter';
import { map, Observable } from 'rxjs';
import { Activity } from '../../../interfaces/model/Activity';
import { PagedRes } from '../../../interfaces/PagedRes';
import { QueryResult } from '../../../interfaces/QueryResult';

@Injectable({
  providedIn: 'root',
})
export class ActivityService {
  // Injectables
  private apollo = inject(Apollo);

  constructor() {}

  getActivities(
    to: string,
    toType: string,
    startDate: string,
    endDate: string,
    filters?: ActivityFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<Activity>>> {
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
    if (filters?.tags && filters.tags.length > 0) {
      const tagsString = `[${filters.tags
        .map((tag) => `"${tag}"`)
        .join(', ')}]`;
      filterQuery += `tags: ${tagsString}, `;
    }
    if (filters?.languages && filters.languages.length > 0) {
      const languagesString = `[${filters.languages
        .map((lang) => `"${lang}"`)
        .join(', ')}]`;
      filterQuery += `languages: ${languagesString}, `;
    }
    if (filters?.minPrice !== undefined) {
      filterQuery += `minPrice: ${filters.minPrice}, `;
    }
    if (filters?.maxPrice !== undefined) {
      filterQuery += `maxPrice: ${filters.maxPrice}, `;
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
            getActivities(
              ${paginationQuery}
              timezone: "${timezone}",
              filters: { 
                  ${toQuery}: "${to}",
                  ${filterQuery}
                  startDate: "${startDate}",
                  endDate: "${endDate}"
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
                notes
                description
                address
                coordinates {
                    lat
                    lng
                }
                destination {
                    city
                    region
                    timezone
                    coordinates {
                        lat
                        lng
                    }
                    country {
                        code
                        name
                    }
                }
                mainPicture
                pictures
                tags
                languages
                price {
                    value
                    currency
                }
                reviewsSummary {
                    avgRating
                    totalCount
                }
                schedule {
                    startDate
                    endDate
                    durationMinutes
                    recurrence {
                        daysOfWeek
                        startTime
                        endTime
                    }
                }
              }
            }
          }
        `;
    return this.apollo
      .watchQuery<{ getActivities: PagedRes<Activity> }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getActivities ?? null,
        })),
      );
  }
}
