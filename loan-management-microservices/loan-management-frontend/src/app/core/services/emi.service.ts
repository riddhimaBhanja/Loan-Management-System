import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { EmiSchedule, EmiScheduleBackendResponse, EmiPayment, EmiPaymentBackendResponse, EmiPaymentRequest } from '../models/emi.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class EmiService {
  private readonly API_URL = `${environment.apiUrl}/emis`;

  constructor(private http: HttpClient) {}

  /**
   * Transform backend EMI schedule response to frontend model
   */
  private transformEmiSchedule(backendEmi: EmiScheduleBackendResponse): EmiSchedule {
    return {
      id: backendEmi.id,
      loanId: backendEmi.loanId,
      customerId: backendEmi.customerId,
      installmentNumber: backendEmi.emiNumber,
      dueDate: backendEmi.dueDate,
      principalAmount: backendEmi.principalComponent,
      interestAmount: backendEmi.interestComponent,
      totalEmi: backendEmi.emiAmount,
      outstandingBalance: backendEmi.outstandingBalance,
      status: backendEmi.status,
      createdAt: backendEmi.createdAt,
      updatedAt: backendEmi.updatedAt
    };
  }

  /**
   * Transform backend payment response to frontend model
   */
  private transformEmiPayment(backendPayment: EmiPaymentBackendResponse): EmiPayment {
    return {
      id: backendPayment.id,
      emiScheduleId: backendPayment.emiScheduleId,
      loanId: backendPayment.loanId,
      emiNumber: backendPayment.emiNumber,
      paymentAmount: backendPayment.amount,
      paymentDate: backendPayment.paymentDate,
      paymentMethod: backendPayment.paymentMethod,
      transactionReference: backendPayment.transactionReference,
      remarks: backendPayment.remarks,
      recordedById: backendPayment.paidBy,
      recordedByName: backendPayment.paidByName,
      createdAt: backendPayment.createdAt
    };
  }

  /**
   * Get EMI schedule for a specific loan
   */
  getEmiSchedule(loanId: number): Observable<ApiResponse<EmiSchedule[]>> {
    return this.http.get<ApiResponse<EmiScheduleBackendResponse[]>>(`${this.API_URL}/loan/${loanId}`).pipe(
      map(response => ({
        ...response,
        data: response.data ? response.data.map(emi => this.transformEmiSchedule(emi)) : []
      }))
    );
  }

  /**
   * Get EMI schedule for all loans of the current user (Customer)
   */
  getMyEmiSchedule(): Observable<ApiResponse<EmiSchedule[]>> {
    return this.http.get<ApiResponse<EmiScheduleBackendResponse[]>>(`${this.API_URL}/my-schedule`).pipe(
      map(response => ({
        ...response,
        data: response.data ? response.data.map(emi => this.transformEmiSchedule(emi)) : []
      }))
    );
  }

  /**
   * Get all pending EMIs for the current customer
   */
  getPendingEmis(): Observable<ApiResponse<EmiSchedule[]>> {
    return this.http.get<ApiResponse<EmiScheduleBackendResponse[]>>(`${this.API_URL}/pending`).pipe(
      map(response => ({
        ...response,
        data: response.data ? response.data.map(emi => this.transformEmiSchedule(emi)) : []
      }))
    );
  }

  /**
   * Get all overdue EMIs across all customers (Admin/Loan Officer only)
   */
  getOverdueEmis(): Observable<ApiResponse<EmiSchedule[]>> {
    return this.http.get<ApiResponse<EmiScheduleBackendResponse[]>>(`${this.API_URL}/overdue`).pipe(
      map(response => ({
        ...response,
        data: response.data ? response.data.map(emi => this.transformEmiSchedule(emi)) : []
      }))
    );
  }

  /**
   * Record EMI payment (Admin/Loan Officer only)
   */
  recordPayment(request: EmiPaymentRequest): Observable<ApiResponse<EmiPayment>> {
    return this.http.post<ApiResponse<EmiPaymentBackendResponse>>(`${this.API_URL}/pay`, request).pipe(
      map(response => ({
        ...response,
        data: response.data ? this.transformEmiPayment(response.data) : {} as EmiPayment
      }))
    );
  }

  /**
   * Get payment history for a specific loan
   */
  getPaymentHistory(loanId: number): Observable<ApiResponse<EmiPayment[]>> {
    return this.http.get<ApiResponse<EmiPaymentBackendResponse[]>>(`${this.API_URL}/loan/${loanId}/payments`).pipe(
      map(response => ({
        ...response,
        data: response.data ? response.data.map(payment => this.transformEmiPayment(payment)) : []
      }))
    );
  }
}
