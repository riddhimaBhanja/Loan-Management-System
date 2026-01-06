export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface ErrorResponse {
  success: false;
  message: string;
  errors?: { [key: string]: string };
  timestamp: string;
}

export interface DashboardStats {
  totalLoans?: number;
  activeLoans?: number;
  closedLoans?: number;
  pendingLoans?: number;
  underReviewLoans?: number;
  approvedLoans?: number;
  rejectedLoans?: number;
  totalDisbursedAmount?: number;
  overdueEmis?: number;
  totalOutstanding?: number;
  nextEmiDueDate?: string;
  nextEmiAmount?: number;
  nextEmiLoanId?: number;
  recentApplications?: Loan[];
  upcomingEmis?: EmiSchedule[];
}

// Backend response interface (different field names)
export interface DashboardBackendResponse {
  // Overall Statistics
  totalLoans?: number;
  totalCustomers?: number;
  pendingApprovals?: number;
  approvedLoans?: number;
  disbursedLoans?: number;
  totalDisbursedAmount?: number;

  // EMI Statistics
  totalEmiCollected?: number;
  pendingEmiAmount?: number;
  overdueAmount?: number;
  overdueCount?: number;

  // Recent Data
  recentLoans?: any[];

  // User Statistics (Admin only)
  totalUsers?: number;
  activeUsers?: number;

  // Customer-specific fields
  myTotalLoans?: number;
  myActiveLoans?: number;
  myClosedLoans?: number;
  myPendingLoans?: number;
  totalOutstanding?: number;
  nextEmiDueDate?: string;
  nextEmiAmount?: number;
  nextEmiLoanId?: number;
  myLoans?: any[];
}

import { Loan } from './loan.model';
import { EmiSchedule } from './emi.model';
