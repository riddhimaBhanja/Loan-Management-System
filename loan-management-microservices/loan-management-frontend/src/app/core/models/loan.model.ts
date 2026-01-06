export enum LoanStatus {
  PENDING = 'PENDING',
  APPLIED = 'APPLIED',
  UNDER_REVIEW = 'UNDER_REVIEW',
  APPROVED = 'APPROVED',
  DISBURSED = 'DISBURSED',
  REJECTED = 'REJECTED',
  CLOSED = 'CLOSED'
}

export enum EmploymentStatus {
  SALARIED = 'SALARIED',
  SELF_EMPLOYED = 'SELF_EMPLOYED',
  BUSINESS_OWNER = 'BUSINESS_OWNER',
  UNEMPLOYED = 'UNEMPLOYED',
  RETIRED = 'RETIRED'
}

export interface Loan {
  id: number;
  applicationNumber: string;
  customerId: number;
  customerName: string;
  loanTypeId: number;
  loanTypeName: string;
  requestedAmount: number;
  approvedAmount?: number;
  tenureMonths: number;
  interestRate?: number;
  emiAmount?: number;
  totalInterest?: number;
  totalPayable?: number;
  status: LoanStatus;
  employmentStatus: EmploymentStatus;
  monthlyIncome: number;
  purpose: string;
  remarks?: string;
  loanOfficerId?: number;
  loanOfficerName?: string;
  appliedAt: string;
  reviewedAt?: string;
  approvedAt?: string;
  disbursedAt?: string;
  disbursementMethod?: string;
  disbursementReference?: string;
  closedAt?: string;
}

export interface LoanApplicationRequest {
  loanTypeId: number;
  amount: number;
  tenureMonths: number;
  employmentStatus: EmploymentStatus;
  monthlyIncome: number;
  purpose: string;
}

export interface LoanApprovalRequest {
  approvedAmount: number;
  interestRate: number;
  remarks?: string;
}

export interface LoanRejectionRequest {
  rejectionReason: string;
  notes?: string;
}

export interface LoanDisbursementRequest {
  disbursementDate: string;
  disbursementMethod: string;
  accountNumber?: string;
  bankName?: string;
  ifscCode?: string;
  referenceNumber?: string;
  remarks?: string;
}

export interface DisbursementDetails {
  id: number;
  loanId: number;
  disbursementDate: string;
  disbursementMethod: string;
  accountNumber?: string;
  bankName?: string;
  ifscCode?: string;
  amount: number;
  status: string;
  remarks?: string;
  disbursedBy?: string;
  createdAt: string;
}
