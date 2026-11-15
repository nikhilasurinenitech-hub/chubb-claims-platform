import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ClaimPage, Detail, Market, CreateClaim, Dashboard, Action } from './models';
@Injectable({ providedIn: 'root' })
export class ClaimsApi {
  private http = inject(HttpClient);
  markets() {
    return this.http.get<Market[]>('/api/markets');
  }
  list(filters: Record<string, string | number> = {}) {
    let params = new HttpParams();
    for (const [k, v] of Object.entries(filters)) if (v !== '') params = params.set(k, v);
    return this.http.get<ClaimPage>('/api/claims', { params });
  }
  detail(id: string) {
    return this.http.get<Detail>('/api/claims/' + id);
  }
  create(body: CreateClaim, key: string) {
    return this.http.post<Detail>('/api/claims', body, { headers: { 'Idempotency-Key': key } });
  }
  act(detail: Detail, action: Action) {
    return this.http.post<Detail>(`/api/claims/${detail.claim.id}/${action.operation}`, {
      version: detail.claim.version,
      note: action.note,
      amount: action.amount,
    });
  }
  respond(detail: Detail, requestId: string, answer: string) {
    return this.http.post<Detail>(
      `/api/claims/${detail.claim.id}/information-requests/${requestId}/response`,
      { version: detail.claim.version, answer },
    );
  }
  dashboard(market = '') {
    return this.http.get<Dashboard>('/api/insights', { params: market ? { market } : {} });
  }
}
