export interface LoanType {
  id: number;
  name: string;
  description: string;
  minAmount: number;
  maxAmount: number;
  minTenureMonths: number;
  maxTenureMonths: number;
  interestRate: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateLoanTypeRequest {
  name: string;
  description: string;
  minAmount: number;
  maxAmount: number;
  minTenureMonths: number;
  maxTenureMonths: number;
  interestRate: number;
}

export interface UpdateLoanTypeRequest {
  name?: string;
  description?: string;
  minAmount?: number;
  maxAmount?: number;
  minTenureMonths?: number;
  maxTenureMonths?: number;
  interestRate?: number;
  isActive?: boolean;
}
