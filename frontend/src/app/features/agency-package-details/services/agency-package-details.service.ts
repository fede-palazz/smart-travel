import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { map, Observable } from 'rxjs';
import { QueryResult } from '../../../interfaces/QueryResult';
import { AgencyPackage } from '../../../interfaces/model/AgencyPackage';
import { PartialAgencyOrderReq } from '../../../interfaces/orders/OrderReq';
import { OrderRes } from '../../../interfaces/orders/OrderRes';

@Injectable({
  providedIn: 'root',
})
export class AgencyPackageDetailsService {
  private apollo = inject(Apollo);

  constructor() {}

  getAgencyPackageDetails(id: string): Observable<QueryResult<AgencyPackage>> {
    const query = gql`
      {
        getAgencyPackageById(id: "${id}") {
           id
            name
            description
            tags
            status
            startDate
            endDate
            mainPicture
            pictures
            creationDate
            quantity
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
            departureFlight {
                flightId
                code
                quantity
                airline
                airlineLogo
                departureTime
                arrivalTime
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
                price {
                    value
                    currency
                }
            }
            returnFlight {
                flightId
                code
                quantity
                airline
                airlineLogo
                departureTime
                arrivalTime
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
                price {
                    value
                    currency
                }
            }
            accommodation {
                accommodationId
                name
                type
                mainPicture
                startDate
                endDate
                rooms {
                    name
                    type
                    capacity
                    quantity
                    amenities
                    bedTypes
                    pictures
                    pricePerNight {
                        value
                        currency
                    }
                }
            }
            activities {
                activityId
                name
                type
                mainPicture
                date
                startTime
                endTime
                price {
                    value
                    currency
                }
                quantity
            }
            agentInfo {
                userId
                email
                name
                surname
            }
        }
      }`;
    return this.apollo
      .watchQuery<{ getAgencyPackageById: AgencyPackage }>({
        query: query,
        errorPolicy: 'all',
      })
      .valueChanges.pipe(
        map(({ data, loading, errors }) => ({
          loading,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.getAgencyPackageById ?? null,
        })),
      );
  }

  performCheckout(agencyPackageId: string): Observable<QueryResult<OrderRes>> {
    const CHECKOUT_MUTATION = gql`
      mutation createAgencyOrder(
        $partialAgencyOrderReq: PartialAgencyOrderReq!
      ) {
        createAgencyOrder(partialAgencyOrderReq: $partialAgencyOrderReq) {
          redirectURL
        }
      }
    `;
    // Create order request
    const orderReq: PartialAgencyOrderReq = {
      agencyPackageId,
    };

    return this.apollo
      .mutate<{ createAgencyOrder: OrderRes }>({
        mutation: CHECKOUT_MUTATION,
        variables: {
          partialAgencyOrderReq: orderReq,
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.createAgencyOrder ?? null,
        })),
      );
  }
}
