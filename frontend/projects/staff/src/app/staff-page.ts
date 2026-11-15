import { Component, inject, signal, OnInit, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, timer } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AuthService,
  ClaimsApi,
  Claim,
  Detail,
  Dashboard,
  Action,
  StatusBadge,
  MoneyPipe,
  Feedback,
  ClaimDetail,
  STATUS_LABELS,
  problem,
} from 'shared';
@Component({
  selector: 'app-staff-page',
  imports: [CommonModule, FormsModule, StatusBadge, MoneyPipe, Feedback, ClaimDetail],
  templateUrl: './staff-page.html',
})
export class StaffPage implements OnInit {
  private destroyRef = inject(DestroyRef);
  readonly auth = inject(AuthService);
  private api = inject(ClaimsApi);
  readonly claims = signal<Claim[]>([]);
  readonly selected = signal<Detail | null>(null);
  readonly dashboard = signal<Dashboard | null>(null);
  readonly loading = signal(false);
  readonly busy = signal(false);
  readonly reportLoading = signal(false);
  readonly error = signal('');
  readonly reportError = signal('');
  readonly notice = signal('');
  readonly statuses = Object.entries(STATUS_LABELS);
  total = 0;
  page = 0;
  status = '';
  market = '';
  assignee = '';
  search = '';
  selectedId = '';
  ngOnInit() {
    this.load();
    this.loadReport();
    timer(5000, 5000)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (document.visibilityState === 'visible' && !this.reportLoading() && !this.busy())
          this.loadReport();
      });
  }
  load() {
    this.loading.set(true);
    this.api
      .list({
        page: this.page,
        size: 10,
        status: this.status,
        market: this.market,
        assignee: this.assignee,
        search: this.search,
      })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (p) => {
          this.claims.set(p.items);
          this.total = p.total;
        },
        error: (e) => this.error.set(problem(e)),
      });
  }
  loadReport() {
    this.reportLoading.set(true);
    this.reportError.set('');
    this.api
      .dashboard(this.market)
      .pipe(finalize(() => this.reportLoading.set(false)))
      .subscribe({
        next: (d) => this.dashboard.set(d),
        error: (e) => this.reportError.set(problem(e)),
      });
  }
  filter() {
    this.page = 0;
    this.error.set('');
    this.load();
    this.loadReport();
  }
  refresh() {
    this.load();
    this.loadReport();
    if (this.selectedId) this.choose(this.selectedId);
  }
  choose(id: string) {
    this.selectedId = id;
    this.error.set('');
    this.api.detail(id).subscribe({
      next: (d) => {
        if (this.selectedId === id) this.selected.set(d);
      },
      error: (e) => {
        if (this.selectedId === id) this.error.set(problem(e));
      },
    });
  }
  close() {
    this.selectedId = '';
    this.selected.set(null);
  }
  act(action: Action) {
    const detail = this.selected();
    if (!detail) return;
    this.busy.set(true);
    this.error.set('');
    this.notice.set('');
    this.api
      .act(detail, action)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: (d) => {
          this.selected.set(d);
          this.notice.set(
            'Claim updated. Refresh the team dashboard after the event is processed.',
          );
          this.load();
          this.loadReport();
        },
        error: (e) => this.error.set(problem(e)),
      });
  }
  next(delta: number) {
    this.page += delta;
    this.load();
  }
  queue(status: string) {
    this.status = status;
    this.filter();
  }
}
