import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import {
  Loan,
  LoanApplicationRequest,
  LoanApprovalRequest,
  LoanRejectionRequest,
  LoanDisbursementRequest,
  DisbursementDetails,
  LoanStatus
} from '../models/loan.model';
import { ApiResponse, PageResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private readonly API_URL = `${environment.apiUrl}/loans`;

  constructor(private http: HttpClient) {}

  submitLoanApplication(request: LoanApplicationRequest): Observable<ApiResponse<Loan>> {
    return this.http.post<ApiResponse<Loan>>(`${this.API_URL}/apply`, request);
  }

  getAllLoans(
    page: number = 0,
    size: number = 10,
    status?: LoanStatus,
    sortBy: string = 'appliedAt',
    sortDir: string = 'desc'
  ): Observable<ApiResponse<PageResponse<Loan>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<ApiResponse<PageResponse<Loan>>>(this.API_URL, { params });
  }

  getMyLoans(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Loan>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PageResponse<Loan>>>(`${this.API_URL}/my-loans`, { params });
  }

  getAssignedLoans(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Loan>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PageResponse<Loan>>>(`${this.API_URL}/assigned`, { params });
  }

  /**
   * Get all pending loan applications awaiting review (Admin/Loan Officer only)
   */
  getPendingLoans(): Observable<ApiResponse<Loan[]>> {
    return this.http.get<ApiResponse<Loan[]>>(`${this.API_URL}/pending`);
  }

  getLoanById(loanId: number): Observable<ApiResponse<Loan>> {
    return this.http.get<ApiResponse<Loan>>(`${this.API_URL}/${loanId}`);
  }

  getLoanByApplicationNumber(applicationNumber: string): Observable<ApiResponse<Loan>> {
    return this.http.get<ApiResponse<Loan>>(`${this.API_URL}/application/${applicationNumber}`);
  }

  markUnderReview(loanId: number): Observable<ApiResponse<Loan>> {
    return this.http.put<ApiResponse<Loan>>(`${this.API_URL}/${loanId}/review`, {});
  }

  approveLoan(loanId: number, request: LoanApprovalRequest): Observable<ApiResponse<Loan>> {
    // Use loan-approvals endpoint for approval
    return this.http.post<ApiResponse<Loan>>(`${environment.apiUrl}/loan-approvals/${loanId}/approve`, request);
  }

  rejectLoan(loanId: number, request: LoanRejectionRequest): Observable<ApiResponse<Loan>> {
    // Use loan-approvals endpoint for rejection
    return this.http.post<ApiResponse<Loan>>(`${environment.apiUrl}/loan-approvals/${loanId}/reject`, request);
  }

  disburseLoan(loanId: number, request: LoanDisbursementRequest): Observable<ApiResponse<Loan>> {
    // Use loan-approvals endpoint for disbursement
    return this.http.post<ApiResponse<Loan>>(`${environment.apiUrl}/loan-approvals/${loanId}/disburse`, request);
  }

  /**
   * Get disbursement details for a loan
   */
  getDisbursementDetails(loanId: number): Observable<ApiResponse<DisbursementDetails>> {
    // Use loan-approvals endpoint for disbursement details
    return this.http.get<ApiResponse<DisbursementDetails>>(`${environment.apiUrl}/loan-approvals/disbursement/loan/${loanId}`);
  }

  closeLoan(loanId: number): Observable<ApiResponse<Loan>> {
    // Use loan-approvals endpoint for closing loan
    return this.http.post<ApiResponse<Loan>>(`${environment.apiUrl}/loan-approvals/${loanId}/close`, {});
  }
}
