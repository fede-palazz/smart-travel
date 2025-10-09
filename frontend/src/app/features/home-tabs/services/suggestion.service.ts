import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { DestinationSuggestion } from '../../../interfaces/filters/DestinationSearch';
import { map, Observable } from 'rxjs';
import { QueryResult } from '../../../interfaces/QueryResult';
import { User } from '../../../interfaces/model/User';

@Injectable({
  providedIn: 'root',
})
export class SuggestionService {
  constructor(private apollo: Apollo) {}

  getDestinationsSuggestions(
    term: string,
  ): Observable<QueryResult<DestinationSuggestion[]>> {
    const query = gql`
      {
        getDestinationsList(name: "${term}") {
          name
          type
        }
      }
    `;
    return this.apollo
      .watchQuery<{ getDestinationsList: DestinationSuggestion[] }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getDestinationsList ?? null,
        })),
      );
  }

  getUsersSuggestions(
    term: string,
    excludeCustomers: boolean = false,
  ): Observable<QueryResult<User[]>> {
    const query = gql`
          {
            getUsersList(name: "${term}", excludeCustomers: ${excludeCustomers}) {
              id
              email
              role
              firstname
              lastname
              fullname
            }
          }
        `;
    return this.apollo
      .watchQuery<{ getUsersList: User[] }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getUsersList ?? null,
        })),
      );
  }
}
