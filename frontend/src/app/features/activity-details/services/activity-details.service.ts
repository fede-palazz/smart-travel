import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { filter, map, Observable, tap } from 'rxjs';
import { QueryResult } from '../../../interfaces/QueryResult';
import { Activity } from '../../../interfaces/model/Activity';

@Injectable({
  providedIn: 'root',
})
export class ActivityDetailsService {
  private apollo = inject(Apollo);

  constructor() {}

  getActivityDetails(id: string): Observable<QueryResult<Activity>> {
    const query = gql`
    {
      getActivityById(id: "${id}") {
        id
        name
        type
        description
        notes
        address
        mainPicture
        pictures
        tags
        languages
        coordinates {
            lat
            lng
        }
        destination {
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
    }`;
    return this.apollo
      .watchQuery<{ getActivityById: Activity }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getActivityById ?? null,
        })),
      );
  }
}
