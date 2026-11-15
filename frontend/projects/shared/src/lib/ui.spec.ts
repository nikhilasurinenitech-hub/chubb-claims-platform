import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { MoneyPipe, StatusBadge, Feedback } from './ui';
import { AuthService, authInterceptor } from './auth';
import { ClaimDetail } from './claim-detail';
import { Detail } from './models';
describe('Shared presentation', () => {
  it('formats currency without combining currencies', () => {
    const money = new MoneyPipe();
    expect(money.transform(1234.5, 'SGD')).toContain('SGD');
    expect(money.transform(1234.5, 'SGD')).toContain('1,234.50');
    expect(money.transform(1000, 'JPY')).toContain('1,000');
    expect(money.transform(1000, 'JPY')).not.toContain('.00');
  });
  it('gives workflow status a readable label', () => {
    const f = TestBed.createComponent(StatusBadge);
    f.componentRef.setInput('value', 'AWAITING_INFORMATION');
    f.detectChanges();
    expect(f.nativeElement.textContent).toContain('Needs information');
  });
  it('announces errors accessibly', () => {
    const f = TestBed.createComponent(Feedback);
    f.componentRef.setInput('message', 'Claim changed. Refresh.');
    f.detectChanges();
    expect(f.nativeElement.querySelector('[role="alert"]').textContent).toContain('Claim changed');
  });
  it('keeps empty feedback out of the page', () => {
    const f = TestBed.createComponent(Feedback);
    f.detectChanges();
    expect(f.nativeElement.querySelector('[role]')).toBeNull();
  });
});
describe('Authentication boundary', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    }),
  );
  it('holds identity in memory and clears it on logout', () => {
    const auth = TestBed.inject(AuthService),
      http = TestBed.inject(HttpTestingController);
    auth.login('maya', 'demo').subscribe();
    const req = http.expectOne('/api/session');
    expect(req.request.headers.get('Authorization')).toBe('Basic ' + btoa('maya:demo'));
    req.flush({ username: 'maya', role: 'CLAIMANT' });
    expect(auth.session()?.username).toBe('maya');
    auth.logout();
    expect(auth.session()).toBeNull();
    expect(auth.header()).toBe('');
    http.verify();
  });
  it('never sends credentials to a non-API resource', () => {
    const auth = TestBed.inject(AuthService),
      http = TestBed.inject(HttpTestingController);
    auth.login('alex', 'demo').subscribe();
    http.expectOne('/api/session').flush({ username: 'alex', role: 'STAFF' });
    TestBed.inject(HttpClient).get('https://example.com/asset').subscribe();
    const req = http.expectOne('https://example.com/asset');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
    http.verify();
  });
  it('clears failed login credentials', () => {
    const auth = TestBed.inject(AuthService),
      http = TestBed.inject(HttpTestingController);
    auth.login('maya', 'bad').subscribe({ error: () => {} });
    http
      .expectOne('/api/session')
      .flush({ detail: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });
    expect(auth.session()).toBeNull();
    expect(auth.header()).toBe('');
    http.verify();
  });
});
describe('Claim action presentation', () => {
  const detail: Detail = {
    claim: {
      id: '1',
      reference: 'CLM-1',
      claimantId: 'maya',
      type: 'MOTOR',
      market: 'SG',
      currency: 'SGD',
      incidentDate: '2026-01-01',
      location: 'Singapore',
      description: 'Vehicle damage reported',
      requestedAmount: 5000,
      status: 'UNDER_REVIEW',
      assignee: 'alex',
      reserveAmount: 0,
      settlementAmount: 0,
      version: 1,
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
      closedAt: null,
    },
    history: [],
    informationRequests: [],
  };
  it('disables approval until an assessment has been saved', () => {
    const f = TestBed.createComponent(ClaimDetail);
    f.componentRef.setInput('detail', detail);
    f.componentRef.setInput('role', 'STAFF');
    f.componentRef.setInput('username', 'alex');
    f.detectChanges();
    const button = Array.from(f.nativeElement.querySelectorAll('button')).find((b: any) =>
      b.textContent.includes('Approve claim'),
    ) as HTMLButtonElement;
    expect(button.disabled).toBe(true);
  });
  it('does not render officer actions for claimants', () => {
    const f = TestBed.createComponent(ClaimDetail);
    f.componentRef.setInput('detail', detail);
    f.componentRef.setInput('role', 'CLAIMANT');
    f.componentRef.setInput('username', 'maya');
    f.detectChanges();
    expect(f.nativeElement.textContent).not.toContain('Save assessment');
  });
  it('preserves an officer draft when a request is pending', () => {
    const f = TestBed.createComponent(ClaimDetail);
    f.componentRef.setInput('detail', detail);
    f.componentRef.setInput('role', 'STAFF');
    f.componentRef.setInput('username', 'alex');
    f.detectChanges();
    f.componentInstance.note = 'Reviewed repair estimate';
    f.componentRef.setInput('busy', true);
    f.detectChanges();
    expect(f.componentInstance.note).toBe('Reviewed repair estimate');
  });
});
