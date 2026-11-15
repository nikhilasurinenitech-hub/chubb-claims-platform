import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { NativeFederationResult } from '@angular-architects/native-federation';
import { authInterceptor } from 'shared';
import { routes } from './app.routes';
export const appConfig = (nf: NativeFederationResult): ApplicationConfig => ({
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideRouter(routes(nf)),
  ],
});
