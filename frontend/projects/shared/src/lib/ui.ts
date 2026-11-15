import { Component, input, Pipe, PipeTransform } from '@angular/core';
import { STATUS_LABELS } from './models';
@Component({
  selector: 'claim-status',
  template: `<span class="badge" [attr.data-status]="value()">{{ label() }}</span>`,
})
export class StatusBadge {
  readonly value = input.required<string>();
  label() {
    return STATUS_LABELS[this.value()] ?? this.value();
  }
}
@Pipe({ name: 'money' })
export class MoneyPipe implements PipeTransform {
  transform(amount: number | string | null, currency = 'SGD') {
    return new Intl.NumberFormat('en-GB', {
      style: 'currency',
      currency,
      currencyDisplay: 'code',
    }).format(Number(amount ?? 0));
  }
}
@Component({
  selector: 'claim-feedback',
  template: `@if (message()) {
    <div
      class="feedback"
      [class.error]="kind() === 'error'"
      [attr.role]="kind() === 'error' ? 'alert' : 'status'"
    >
      {{ message() }}
    </div>
  }`,
})
export class Feedback {
  readonly message = input('');
  readonly kind = input<'error' | 'notice'>('error');
}
