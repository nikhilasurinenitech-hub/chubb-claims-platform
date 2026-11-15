export type Role = 'CLAIMANT' | 'STAFF';
export interface Session {
  username: string;
  role: Role;
}
export interface Claim {
  id: string;
  reference: string;
  claimantId: string;
  type: string;
  market: string;
  currency: string;
  incidentDate: string;
  location: string;
  description: string;
  requestedAmount: number;
  status: string;
  assignee: string | null;
  reserveAmount: number;
  settlementAmount: number;
  version: number;
  createdAt: string;
  updatedAt: string;
  closedAt: string | null;
}
export interface History {
  id: number;
  actor: string;
  action: string;
  note: string;
  occurredAt: string;
}
export interface InformationRequest {
  id: string;
  question: string;
  requestedBy: string;
  requestedAt: string;
  answer: string | null;
  answeredAt: string | null;
}
export interface Detail {
  claim: Claim;
  history: History[];
  informationRequests: InformationRequest[];
}
export interface ClaimPage {
  items: Claim[];
  total: number;
  page: number;
  size: number;
}
export interface Market {
  code: string;
  name: string;
  currency: string;
}
export interface CreateClaim {
  type: string;
  market: string;
  incidentDate: string;
  location: string;
  description: string;
  requestedAmount: number;
}
export interface Action {
  operation: string;
  note: string;
  amount: number | null;
}
export interface Dashboard {
  totalClaims: number;
  openClaims: number;
  closedClaims: number;
  averageClosureHours: number;
  byStatus: Record<string, number>;
  officers: { username: string; assigned: number; open: number; waiting: number; closed: number }[];
  exposure: {
    currency: string;
    outstandingReserve: number;
    settledAmount: number;
    unassessedClaims: number;
  }[];
  lastUpdated: string | null;
}
export const STATUS_LABELS: Record<string, string> = {
  SUBMITTED: 'Submitted',
  UNDER_REVIEW: 'Under review',
  AWAITING_INFORMATION: 'Needs information',
  APPROVED: 'Approved',
  SETTLED: 'Settled',
  REJECTED: 'Rejected',
};
