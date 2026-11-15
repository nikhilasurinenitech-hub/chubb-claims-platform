import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import {
  AuthService,
  ClaimsApi,
  Claim,
  Detail,
  Market,
  CreateClaim,
  StatusBadge,
  MoneyPipe,
  Feedback,
  ClaimDetail,
  problem,
} from 'shared';
@Component({
  selector: 'app-claimant-page',
  imports: [CommonModule, FormsModule, StatusBadge, MoneyPipe, Feedback, ClaimDetail],
  templateUrl: './claimant-page.html',
})
export class ClaimantPage implements OnInit {
  readonly auth = inject(AuthService);
  private api = inject(ClaimsApi);
  readonly claims = signal<Claim[]>([]);
  readonly selected = signal<Detail | null>(null);
  readonly markets = signal<Market[]>([]);
  readonly loading = signal(false);
  readonly busy = signal(false);
  readonly error = signal('');
  readonly notice = signal('');
  readonly showForm = signal(false);
  total = 0;
  page = 0;
  status = '';
  selectedId = '';
  today = new Date().toISOString().slice(0, 10);
  private key = crypto.randomUUID();
  private lastPayload = '';
  form: CreateClaim = {
    type: 'MOTOR',
    market: 'SG',
    incidentDate: this.today,
    location: '',
    description: '',
    requestedAmount: 0,
  };
  ngOnInit() {
    this.api
      .markets()
      .subscribe({ next: (m) => this.markets.set(m), error: (e) => this.error.set(problem(e)) });
    this.load();
  }
  currency() {
    return this.markets().find((m) => m.code === this.form.market)?.currency ?? 'SGD';
  }
  load() {
    this.loading.set(true);
    this.error.set('');
    this.api
      .list({ page: this.page, size: 10, status: this.status })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (p) => {
          this.claims.set(p.items);
          this.total = p.total;
        },
        error: (e) => this.error.set(problem(e)),
      });
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
  filter() {
    this.page = 0;
    this.load();
  }
  create() {
    this.busy.set(true);
    this.error.set('');
    this.notice.set('');
    const payload = JSON.stringify(this.form);
    if (payload !== this.lastPayload) {
      this.key = crypto.randomUUID();
      this.lastPayload = payload;
    }
    this.api
      .create({ ...this.form }, this.key)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: (d) => {
          this.showForm.set(false);
          this.selectedId = d.claim.id;
          this.selected.set(d);
          this.notice.set(
            'Claim ' + d.claim.reference + ' submitted. You can follow every update here.',
          );
          this.form = { ...this.form, location: '', description: '', requestedAmount: 0 };
          this.lastPayload = '';
          this.page = 0;
          this.load();
        },
        error: (e) => this.error.set(problem(e)),
      });
  }
  respond(event: { requestId: string; answer: string }) {
    const detail = this.selected();
    if (!detail) return;
    this.busy.set(true);
    this.error.set('');
    this.api
      .respond(detail, event.requestId, event.answer)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: (d) => {
          this.selected.set(d);
          this.notice.set('Your response has been sent to the assigned officer.');
          this.load();
        },
        error: (e) => this.error.set(problem(e)),
      });
  }
  next(delta: number) {
    this.page += delta;
    this.load();
  }
}
