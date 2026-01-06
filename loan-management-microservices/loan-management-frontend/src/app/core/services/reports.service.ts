import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface LoanSummaryReport {
  totalLoans: number;
  totalAmount: number;
  approvedLoans: number;
  approvedAmount: number;
  rejectedLoans: number;
  pendingLoans: number;
  disbursedLoans: number;
  disbursedAmount: number;
  startDate: string;
  endDate: string;
}

export interface EmiCollectionReport {
  totalEmisDue: number;
  totalAmountDue: number;
  totalEmisCollected: number;
  totalAmountCollected: number;
  totalEmisOverdue: number;
  totalAmountOverdue: number;
  collectionRate: number;
  startDate: string;
  endDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private readonly API_URL = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  /**
   * Get loan summary report for a date range (Admin/Officer only)
   */
  getLoanSummaryReport(startDate: string, endDate: string): Observable<ApiResponse<LoanSummaryReport>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<LoanSummaryReport>>(`${this.API_URL}/loans/summary`, { params });
  }

  /**
   * Get EMI collection report for a date range (Admin/Officer only)
   */
  getEmiCollectionReport(startDate: string, endDate: string): Observable<ApiResponse<EmiCollectionReport>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<EmiCollectionReport>>(`${this.API_URL}/emis/collection`, { params });
  }
}
