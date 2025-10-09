import { inject, Injector } from '@angular/core';
import { HttpLink } from 'apollo-angular/http';
import { environment } from './environments/environment';
import { setContext } from '@apollo/client/link/context';
import {
  ApolloClientOptions,
  ApolloLink,
  InMemoryCache,
} from '@apollo/client/core';
import { TokenService } from './app/services/token.service';
import { onError } from '@apollo/client/link/error';
import { AuthService } from './app/services/auth.service';
import { firstValueFrom } from 'rxjs';

export const getApolloOptions = (): ApolloClientOptions<any> => {
  const httpLink = inject(HttpLink);
  const injector = inject(Injector);
  const tokenService = inject(TokenService);

  const authLink = setContext((_request, previousContext) => {
    // console.log('Auth link stage');
    const accessToken = tokenService.getAccessToken();
    return accessToken
      ? {
          ...previousContext,
          headers: { Authorization: `Bearer ${accessToken}` },
        }
      : { ...previousContext };
  });

  const refreshTokenLink = getRefreshTokenLink(injector);

  const link = ApolloLink.from([
    authLink,
    //refreshTokenLink,
    httpLink.create({ uri: environment.graphqlEndpoint }),
  ]);

  return {
    link,
    cache: new InMemoryCache({ addTypename: false }),
    // defaultOptions: {
    //   watchQuery: {
    //     fetchPolicy: 'cache-and-network',
    //     errorPolicy: 'all',
    //   },
    //   query: {
    //     fetchPolicy: 'network-only',
    //     errorPolicy: 'all',
    //   },
    //   mutate: {
    //     errorPolicy: 'all',
    //   },
    // },
  };
};

const getRefreshTokenLink = (injector: Injector): ApolloLink => {
  return onError(({ networkError, operation, forward }) => {
    const isTokenExpired =
      networkError &&
      'status' in networkError &&
      networkError.status === 401 &&
      'error' in networkError &&
      networkError.error === 'Expired access token';

    if (!isTokenExpired) return;

    const authService = injector.get(AuthService); // Lazy access AuthService
    const oldHeaders = operation.getContext()['headers'] || {};

    // Remove Authorization header to allow refresh
    operation.setContext({
      ...operation.getContext(),
      headers: {},
    });
    console.log('onError stage');
    console.log(oldHeaders);

    operation.setContext(async (_: any) => {
      const tokens = await firstValueFrom(authService.refreshTokens());
      console.log(tokens);
      // Return the headers as usual
      return {
        headers: {
          //...oldHeaders,
          Authorization: `Bearer ${tokens?.accessToken}`,
        },
      };
    });
    return forward(operation);
  });
};
