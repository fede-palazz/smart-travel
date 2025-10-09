import { inject, Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import {
  BehaviorSubject,
  catchError,
  filter,
  map,
  Observable,
  of,
  tap,
} from 'rxjs';
import { LoginRes } from '../interfaces/auth/LoginRes';
import { User, UserRole } from '../interfaces/model/User';
import { TokenService } from './token.service';
import { QueryResult } from '../interfaces/QueryResult';

interface RefreshResult {
  accessToken: string;
  refreshToken: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // State variables
  private userSubject = new BehaviorSubject<User | null>(null);
  private authLoadingSubject = new BehaviorSubject<boolean>(true);
  user$: Observable<User | null>;

  // Injectables
  private tokenService = inject(TokenService);
  private apollo = inject(Apollo);

  constructor() {
    this.user$ = this.userSubject.asObservable().pipe(
      filter(() => !this.authLoadingSubject.getValue()), // only emit when not loading
    );
    // this.user$.subscribe((u) => console.log(u));
  }

  initAuth(): Observable<QueryResult<User | null>> {
    if (!this.tokenService.getAccessToken()) {
      this.authLoadingSubject.next(false);
      return of({
        data: null,
        error: null,
        loading: false,
      });
    }
    return this.fetchUserProfile().pipe(
      tap((_) => {
        this.authLoadingSubject.next(false);
      }),
    );
  }

  getUser(): User | null {
    return this.userSubject.value;
  }

  setUser(user: User) {
    this.userSubject.next(user);
  }

  isLoggedIn(): boolean {
    return this.tokenService.hasAccessToken() && !!this.getUser();
  }

  isCustomer(): boolean {
    return this.isLoggedIn() && this.getUser()!.role === UserRole.CUSTOMER;
  }

  isAgent(): boolean {
    return this.isLoggedIn() && this.getUser()!.role === UserRole.AGENT;
  }

  isAdmin(): boolean {
    return this.isLoggedIn() && this.getUser()!.role === UserRole.ADMIN;
  }

  login(email: string, password: string): Observable<QueryResult<LoginRes>> {
    const LOGIN_MUTATION = gql`
      mutation login($loginReq: LoginReq!) {
        login(loginReq: $loginReq) {
          accessToken
          refreshToken
          user {
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
    return this.apollo
      .mutate<{ login: LoginRes }>({
        mutation: LOGIN_MUTATION,
        variables: {
          loginReq: { email, password },
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.login ?? null,
        })),
        tap(({ data }) => {
          if (data) {
            this.tokenService.setAccessToken(data.accessToken);
            this.tokenService.setRefreshToken(data.refreshToken);
            this.userSubject.next(data.user);
          }
        }),
        catchError((err) =>
          of({
            loading: false,
            data: null,
            error: err.message || 'Unknown error',
          }),
        ),
      );
  }

  logout() {
    this.tokenService.clearTokens();
    this.userSubject.next(null);
    this.apollo.client.resetStore();
  }

  fetchUserProfile(): Observable<QueryResult<User | null>> {
    const PROFILE_QUERY = gql`
      {
        profile {
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
      .query<{ profile: User }>({
        query: PROFILE_QUERY,
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
      })
      .pipe(
        map(({ data, loading, errors }) => ({
          loading: loading ?? false,
          error: errors && errors[0] ? errors[0].message : null,
          data: data?.profile ?? null,
        })),
        catchError((err: any) => {
          // Handle network errors
          return of({
            loading: false,
            error: err.message ?? 'Unknown error',
            data: null,
          });
        }),
      );
  }

  refreshTokens(): Observable<RefreshResult | null> {
    const accessToken = this.tokenService.getAccessToken();
    const refreshToken = this.tokenService.getRefreshToken();
    if (!accessToken || !refreshToken) {
      return of(null);
    }
    const REFRESH_MUTATION = gql`
      mutation refresh($refreshTokenReq: RefreshTokenReq!) {
        refresh(refreshTokenReq: $refreshTokenReq) {
          accessToken
          refreshToken
        }
      }
    `;
    return this.apollo
      .mutate<{ refresh: RefreshResult }>({
        mutation: REFRESH_MUTATION,
        variables: {
          refreshTokenReq: { accessToken, refreshToken },
        },
        errorPolicy: 'all',
        fetchPolicy: 'no-cache',
      })
      .pipe(
        map(({ data, errors }) => {
          if (errors && errors[0]) {
            throw new Error(errors[0].message);
          }
          return data?.refresh ?? null;
        }),
        // Apply TS Type Guard to avoid null value error
        filter((tokens): tokens is RefreshResult => !!tokens),
        tap((tokens) => {
          this.tokenService.setAccessToken(tokens.accessToken);
          this.tokenService.setRefreshToken(tokens.refreshToken);
        }),
        catchError((err) => {
          // Refresh token expired
          console.log('Error: logging out...');
          console.error(err);
          this.logout();
          return of(null);
        }),
      );
  }
}
