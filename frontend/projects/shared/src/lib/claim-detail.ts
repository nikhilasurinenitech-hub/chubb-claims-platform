import { Component, input, output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Detail, Action, Role } from './models';
import { StatusBadge, MoneyPipe } from './ui';
@Component({
  selector: 'claim-detail',
  imports: [CommonModule, FormsModule, StatusBadge, MoneyPipe],
  templateUrl: './claim-detail.html',
})
export class ClaimDetail implements OnChanges {
  readonly detail = input.required<Detail>();
  readonly role = input.required<Role>();
  readonly username = input.required<string>();
  readonly busy = input(false);
  readonly action = output<Action>();
  readonly response = output<{ requestId: string; answer: string }>();
  readonly close = output<void>();
  note = '';
  answer = '';
  amount: number | null = null;
  ngOnChanges(changes: SimpleChanges) {
    if (!changes['detail']) return;
    this.note = '';
    this.answer = '';
    this.amount = this.detail().claim.reserveAmount || this.detail().claim.requestedAmount;
  }
  own() {
    return this.detail().claim.assignee === this.username();
  }
  openRequest() {
    return this.detail().informationRequests.find((r) => !r.answeredAt);
  }
  emit(operation: string) {
    this.action.emit({ operation, note: this.note, amount: this.amount });
  }
  answerRequest() {
    const request = this.openRequest();
    if (request) this.response.emit({ requestId: request.id, answer: this.answer });
  }
}
