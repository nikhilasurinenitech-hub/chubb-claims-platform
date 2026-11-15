import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, tap, throwError } from 'rxjs';
import { Session } from './models';
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly session = signal<Session | null>(null);
  private authorization = '';
  header() {
    return this.authorization;
  }
  login(username: string, password: string) {
    this.authorization = 'Basic ' + btoa(username + ':' + password);
    return this.http.get<Session>('/api/session').pipe(
      tap((s) => this.session.set(s)),
      catchError((e) => {
        this.logout();
        return throwError(() => e);
      }),
    );
  }
  logout() {
    this.authorization = '';
    this.session.set(null);
  }
}
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  return next(
    request.url.startsWith('/api/') && auth.header()
      ? request.clone({ setHeaders: { Authorization: auth.header() } })
      : request,
  );
};
export function problem(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0)
      return 'Cannot reach the service. Check that the local containers are running, then retry.';
    const fields = error.error?.errors as Record<string, string> | undefined;
    if (fields)
      return Object.entries(fields)
        .map(([k, v]) => k + ': ' + v)
        .join('. ');
    return error.error?.detail ?? 'The request could not be completed. Please retry.';
  }
  return 'Something went wrong. Please retry.';
}
