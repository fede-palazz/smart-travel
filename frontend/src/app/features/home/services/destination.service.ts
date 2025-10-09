import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { map, Observable } from 'rxjs';
import { DestinationPreview } from '../../../interfaces/model/Destination';
import { QueryResult } from '../../../interfaces/QueryResult';
import { PagedRes } from '../../../interfaces/PagedRes';

interface GetDestinationsPreviewsResponse {}

@Injectable({
  providedIn: 'root',
})
export class DestinationService {
  private apollo = inject(Apollo);

  constructor() {}

  getDestinationsPreviews(): Observable<
    QueryResult<PagedRes<DestinationPreview>>
  > {
    const query = gql`
      {
        getDestinations(order: "desc", sort: "popularityScore") {
          content {
            id
            city
            description
            pictures
            country {
              name
              code
            }
          }
        }
      }
    `;
    return this.apollo
      .watchQuery<{ getDestinations: PagedRes<DestinationPreview> }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getDestinations ?? null,
        })),
      );
  }
}
