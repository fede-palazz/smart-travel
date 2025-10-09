import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { map, Observable } from 'rxjs';
import { Accommodation } from '../../../interfaces/model/Accommodation';
import { QueryResult } from '../../../interfaces/QueryResult';

@Injectable({
  providedIn: 'root',
})
export class AccommodationDetailsService {
  private apollo = inject(Apollo);

  constructor() {}

  getAccommodationDetails(id: string): Observable<QueryResult<Accommodation>> {
    const query = gql`
    {
      getAccommodationDetailsById(id: "${id}") {
        checkInTime
        checkOutTime
        address
        contacts {
          email
          phone
          website
        }
        coordinates {
          lat
          lng
        }
        description
        details
        distanceToCenterKm
        id
        languages
        mainPicture
        name
        pictures
        policies {
          cancellation
          payment
          pets
        }
        reviewsSummary {
          avgRating
          totalCount
        }
        rooms {
          amenities
          bedTypes
          name
          capacity
          pictures
          pricePerNight {
            currency
            value
          }
          quantity
          type
        }
        services
        type
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
      }
    }`;
    return this.apollo
      .watchQuery<{ getAccommodationDetailsById: Accommodation }>({
        query: query,
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getAccommodationDetailsById ?? null,
        })),
      );
  }
}
