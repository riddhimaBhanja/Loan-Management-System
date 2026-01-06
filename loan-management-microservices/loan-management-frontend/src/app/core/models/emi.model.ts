export enum EmiStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE'
}

export enum PaymentMethod {
  CASH = 'CASH',
  CHEQUE = 'CHEQUE',
  NEFT = 'NEFT',
  RTGS = 'RTGS',
  UPI = 'UPI',
  DEBIT_CARD = 'DEBIT_CARD',
  CREDIT_CARD = 'CREDIT_CARD',
  NET_BANKING = 'NET_BANKING',
  DEMAND_DRAFT = 'DEMAND_DRAFT'
}

export interface EmiSchedule {
  id: number;
  loanId: number;
  customerId?: number;
  installmentNumber: number;
  dueDate: string;
  principalAmount: number;
  interestAmount: number;
  totalEmi: number;
  outstandingBalance: number;
  status: EmiStatus;
  createdAt?: string;
  updatedAt?: string;
}

// Backend response interface (different field names)
export interface EmiScheduleBackendResponse {
  id: number;
  loanId: number;
  customerId?: number;
  emiNumber: number;
  dueDate: string;
  principalComponent: number;
  interestComponent: number;
  emiAmount: number;
  outstandingBalance: number;
  status: EmiStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface EmiPayment {
  id: number;
  emiScheduleId: number;
  loanId?: number;
  emiNumber?: number;
  paymentAmount: number;
  paymentDate: string;
  paymentMethod: PaymentMethod;
  transactionReference: string;
  remarks?: string;
  recordedById: number;
  recordedByName: string;
  createdAt: string;
}

// Backend response interface for payments (different field names)
export interface EmiPaymentBackendResponse {
  id: number;
  emiScheduleId: number;
  loanId?: number;
  emiNumber?: number;
  amount: number;
  paymentDate: string;
  paymentMethod: PaymentMethod;
  transactionReference: string;
  remarks?: string;
  paidBy: number;
  paidByName: string;
  createdAt: string;
}

export interface EmiPaymentRequest {
  emiScheduleId: number;
  amount: number;
  paymentDate: string;
  paymentMethod: PaymentMethod;
  transactionReference: string;
  remarks?: string;
}
