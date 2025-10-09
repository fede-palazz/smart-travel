import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { UserFilter } from '../../../../interfaces/filters/UserFilter';
import { User, UserReq } from '../../../../interfaces/model/User';
import { PaginationFilter } from '../../../../interfaces/filters/PaginationFilter';
import { Observable, map } from 'rxjs';
import { PagedRes } from '../../../../interfaces/PagedRes';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { OkRes } from '../../../../interfaces/OkRes';

const GET_USERS_QUERY = (
  paginationQuery: string = '',
  filterQuery: string = '',
) => gql`
      {
        getUsers(
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
            email
            role
            firstname
            lastname
            fullname
          }
        }
      }
    `;

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private apollo: Apollo) {}

  getUsers(
    filters?: UserFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<User>>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    return this.apollo
      .watchQuery<{
        getUsers: PagedRes<User>;
      }>({
        query: GET_USERS_QUERY(paginationQuery, filterQuery),
        fetchPolicy: 'network-only',
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getUsers ?? null,
        })),
      );
  }

  addUser(
    userReq: UserReq,
    filters?: UserFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<OkRes>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    const ADD_USER_MUTATION = gql`
      mutation addUser($userReq: UserReq!) {
        addUser(userReq: $userReq) {
          id
        }
      }
    `;
    return this.apollo
      .mutate<{ addUser: OkRes }>({
        mutation: ADD_USER_MUTATION,
        variables: {
          userReq: userReq,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
        refetchQueries: [
          { query: GET_USERS_QUERY(paginationQuery, filterQuery) },
        ],
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.addUser ?? null,
        })),
      );
  }

  deleteUser(
    id: string,
    filters?: UserFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<OkRes>> {
    const paginationQuery = pagination
      ? this.getPaginationQuery(pagination)
      : '';
    const filterQuery = filters ? this.getFilterQuery(filters) : '';
    const DELETE_MUTATION = gql`
      mutation deleteUser($id: ID!) {
        deleteUser(id: $id) {
          id
        }
      }
    `;
    return this.apollo
      .mutate<{ deleteUser: OkRes }>({
        mutation: DELETE_MUTATION,
        variables: {
          id: id,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
        refetchQueries: [
          { query: GET_USERS_QUERY(paginationQuery, filterQuery) },
        ],
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.deleteUser ?? null,
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

  private getFilterQuery(filters: UserFilter): string {
    // Compose filter query
    let filterQuery = '';

    if (filters.id !== undefined) {
      filterQuery += `id: "${filters.id}", `;
    }
    if (filters.name !== undefined) {
      filterQuery += `name: "${filters.name}", `;
    }
    if (filters.email !== undefined) {
      filterQuery += `email: "${filters.email}", `;
    }
    if (filters.role !== undefined) {
      filterQuery += `role: "${filters.role}", `;
    }

    return filterQuery;
  }
}
