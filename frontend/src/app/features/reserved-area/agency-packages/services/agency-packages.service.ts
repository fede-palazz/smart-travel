import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { FullAgencyPackageFilter } from '../../../../interfaces/filters/AgencyPackageFilter';
import { PaginationFilter } from '../../../../interfaces/filters/PaginationFilter';
import { AgencyPackagePreview } from '../../../../interfaces/model/AgencyPackagePreview';
import { PagedRes } from '../../../../interfaces/PagedRes';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { AgencyPackage } from '../../../../interfaces/model/AgencyPackage';
import { OkRes } from '../../../../interfaces/OkRes';
import { AgencyPackageReq } from '../../../../interfaces/orders/AgencyPackageReq';

const GET_AGENCY_PACKAGES_QUERY = (
  paginationQuery: string = '',
  filterQuery: string = '',
) => gql`
      {
        getAgencyPackages(
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
            name
            description
            tags
            status
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
            agentInfo {
                userId
                email
                name
                surname
            }
            creationDate
            quantity
          }
        }
      }
    `;

@Injectable({
  providedIn: 'root',
})
export class AgencyPackagesService {
  private apollo = inject(Apollo);

  constructor() {}

  getAgencyPackagesPreview(
    filters?: FullAgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<AgencyPackagePreview>>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    return this.apollo
      .watchQuery<{
        getAgencyPackages: PagedRes<AgencyPackage>;
      }>({
        query: GET_AGENCY_PACKAGES_QUERY(paginationQuery, filterQuery),
        fetchPolicy: 'network-only',
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getAgencyPackages ?? null,
        })),
      );
  }

  archivePackage(
    id: string,
    filters?: FullAgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<OkRes>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    const ARCHIVE_MUTATION = gql`
      mutation archiveAgencyPackage($id: ID!) {
        archiveAgencyPackage(id: $id) {
          id
        }
      }
    `;
    return this.apollo
      .mutate<{ archiveAgencyPackage: OkRes }>({
        mutation: ARCHIVE_MUTATION,
        variables: {
          id: id,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
        refetchQueries: [
          { query: GET_AGENCY_PACKAGES_QUERY(paginationQuery, filterQuery) },
        ],
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.archiveAgencyPackage ?? null,
        })),
      );
  }

  publishPackage(
    id: string,
    filters?: FullAgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<OkRes>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    const PUBLISH_MUTATION = gql`
      mutation publishAgencyPackage($id: ID!) {
        publishAgencyPackage(id: $id) {
          id
        }
      }
    `;
    return this.apollo
      .mutate<{ publishAgencyPackage: OkRes }>({
        mutation: PUBLISH_MUTATION,
        variables: {
          id: id,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
        refetchQueries: [
          { query: GET_AGENCY_PACKAGES_QUERY(paginationQuery, filterQuery) },
        ],
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.publishAgencyPackage ?? null,
        })),
      );
  }

  deletePackage(
    id: string,
    filters?: FullAgencyPackageFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<OkRes>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    const DELETE_MUTATION = gql`
      mutation deleteAgencyPackage($id: ID!) {
        deleteAgencyPackage(id: $id) {
          id
        }
      }
    `;
    return this.apollo
      .mutate<{ deleteAgencyPackage: OkRes }>({
        mutation: DELETE_MUTATION,
        variables: {
          id: id,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
        refetchQueries: [
          { query: GET_AGENCY_PACKAGES_QUERY(paginationQuery, filterQuery) },
        ],
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.deleteAgencyPackage ?? null,
        })),
      );
  }

  addAgencyPackage(
    packageReq: AgencyPackageReq,
  ): Observable<QueryResult<OkRes>> {
    const ADD_PACKAGE_MUTATION = gql`
      mutation addAgencyPackage($packageReq: AgencyPackageReq!) {
        addAgencyPackage(packageReq: $packageReq) {
          id
        }
      }
    `;
    return this.apollo
      .mutate<{ addAgencyPackage: OkRes }>({
        mutation: ADD_PACKAGE_MUTATION,
        variables: {
          packageReq: packageReq,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.addAgencyPackage ?? null,
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

  private getFilterQuery(filters: FullAgencyPackageFilter): string {
    // Compose filter query
    let filterQuery = '';

    if (filters.to !== undefined && filters.toType !== undefined) {
      const toQuery =
        filters.toType === 'city'
          ? 'city'
          : filters.toType === 'region'
            ? 'region'
            : 'country';
      filterQuery += `${toQuery}: "${filters.to}",`;
    }
    if (filters.startDate) {
      filterQuery += `startDate: "${filters.startDate}",`;
    }
    if (filters.endDate) {
      filterQuery += `endDate: "${filters.endDate}",`;
    }

    if (filters.name !== undefined) {
      filterQuery += `name: "${filters.name}", `;
    }
    if (filters.tags && filters.tags.length > 0) {
      const typesString = `[${filters.tags
        .map((tag) => `"${tag}"`)
        .join(', ')}]`;
      filterQuery += `tags: ${typesString}, `;
    }
    if (filters.status !== undefined) {
      filterQuery += `status: "${filters.status}", `;
    }
    if (filters.minPrice !== undefined) {
      filterQuery += `minPrice: ${filters.minPrice}, `;
    }
    if (filters.maxPrice !== undefined) {
      filterQuery += `maxPrice: ${filters.maxPrice}, `;
    }
    if (filters.authorId !== undefined) {
      filterQuery += `authorId: "${filters.authorId}", `;
    }
    return filterQuery;
  }
}
