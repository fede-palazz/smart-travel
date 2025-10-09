import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { AgencyPackageFilter } from '../../../interfaces/filters/AgencyPackageFilter';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { map, Observable } from 'rxjs';
import { QueryResult } from '../../../interfaces/QueryResult';
import { PagedRes } from '../../../interfaces/PagedRes';
import { AgencyPackagePreview } from '../../../interfaces/model/AgencyPackagePreview';

@Injectable({
  providedIn: 'root',
})
export class AgencyPackageService {
  private apollo = inject(Apollo);

  constructor() {}

  getAgencyPackages(
    to: string,
    toType: string,
    startDate: string,
    endDate: string,
    filters?: AgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<AgencyPackagePreview>>> {
    const toQuery =
      toType === 'city' ? 'city' : toType === 'region' ? 'region' : 'country';

    // Compose filter query
    let filterQuery = '';
    if (filters?.name !== undefined) {
      filterQuery += `name: "${filters.name}", `;
    }
    if (filters?.tags && filters.tags.length > 0) {
      const typesString = `[${filters.tags
        .map((tag) => `"${tag}"`)
        .join(', ')}]`;
      filterQuery += `tags: ${typesString}, `;
    }
    if (filters?.status !== undefined) {
      filterQuery += `status: "${filters.status}", `;
    }
    if (filters?.minPrice !== undefined) {
      filterQuery += `minPrice: ${filters.minPrice}, `;
    }
    if (filters?.maxPrice !== undefined) {
      filterQuery += `maxPrice: ${filters.maxPrice}, `;
    }
    if (filters?.authorId !== undefined) {
      filterQuery += `authorId: "${filters.authorId}", `;
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

    // Assemble query
    const query = gql`
          {
            getAgencyPackages(
              ${paginationQuery}
              filters: { 
                  ${toQuery}: "${to}",
                  ${filterQuery}
                  startDate: "${startDate}",
                  endDate: "${endDate}",
              }
            ) {
              currentPage
              elementsInPage
              totalElements
              totalPages
              content {
                id
                name
                description
                startDate
                endDate
                totalPrice {
                    value
                    currency
                }
                destination {
                    destinationId
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
                mainPicture
                quantity
              }
            }
          }
        `;
    return this.apollo
      .watchQuery<{ getAgencyPackages: PagedRes<AgencyPackagePreview> }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getAgencyPackages ?? null,
        })),
      );
  }
}
