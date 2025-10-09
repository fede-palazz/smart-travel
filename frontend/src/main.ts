import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideApollo } from 'apollo-angular';
import { APP_INITIALIZER } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { MessageService, PrimeNGConfig } from 'primeng/api';
import { getApolloOptions } from './apolloConfig';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideAnimations(),
    provideHttpClient(),
    provideApollo(getApolloOptions, {
      useInitialLoading: true, // enable loading state
      useMutationLoading: true,
    }),
    {
      provide: APP_INITIALIZER,
      useFactory: (config: PrimeNGConfig) => () => {
        config.ripple = true;
      },
      deps: [PrimeNGConfig],
      multi: true,
    },
    MessageService,
  ],
}).catch((err) => console.error(err));
