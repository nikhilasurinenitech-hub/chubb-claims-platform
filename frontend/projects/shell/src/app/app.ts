import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthService, Feedback, problem } from 'shared';
@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, FormsModule, Feedback],
  templateUrl: './app.html',
})
export class App {
  readonly auth = inject(AuthService);
  private router = inject(Router);
  readonly busy = signal(false);
  readonly error = signal('');
  username = 'maya';
  password = 'ClaimsDemo!2026';
  login() {
    this.busy.set(true);
    this.error.set('');
    this.auth
      .login(this.username, this.password)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: (s) => {
          this.password = '';
          void this.router.navigate([s.role === 'STAFF' ? '/staff' : '/claimant']);
        },
        error: (e) => this.error.set(problem(e)),
      });
  }
  logout() {
    this.auth.logout();
    this.password = 'ClaimsDemo!2026';
    void this.router.navigate(['/']);
  }
}
