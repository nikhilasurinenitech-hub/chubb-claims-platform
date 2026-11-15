import { inject, Type, Component } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { NativeFederationResult } from '@angular-architects/native-federation';
import { AuthService, Role } from 'shared';
@Component({
  template: `<section class="panel empty">
    <h1>This workspace could not load</h1>
    <p>Check the web container and reload the page. Your saved claims remain in the database.</p>
    <button (click)="reload()">Reload workspace</button>
  </section>`,
})
class RemoteUnavailable {
  reload() {
    location.reload();
  }
}
const roleGuard =
  (role: Role): CanActivateFn =>
  () => {
    const session = inject(AuthService).session(),
      router = inject(Router);
    return session?.role === role
      ? true
      : router.createUrlTree([session?.role === 'STAFF' ? '/staff' : session ? '/claimant' : '/']);
  };
export const routes = (nf: NativeFederationResult): Routes => [
  {
    path: 'claimant',
    canActivate: [roleGuard('CLAIMANT')],
    loadComponent: () =>
      nf
        .as<{ App: Type<unknown> }>()
        .loadRemoteModule('claimant', './Component')
        .then((m) => m.App)
        .catch(() => RemoteUnavailable),
  },
  {
    path: 'staff',
    canActivate: [roleGuard('STAFF')],
    loadComponent: () =>
      nf
        .as<{ App: Type<unknown> }>()
        .loadRemoteModule('staff', './Component')
        .then((m) => m.App)
        .catch(() => RemoteUnavailable),
  },
  { path: '', pathMatch: 'full', children: [] },
  { path: '**', redirectTo: '' },
];
