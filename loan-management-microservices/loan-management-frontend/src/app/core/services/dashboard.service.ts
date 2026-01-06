import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { ApiResponse, DashboardStats, DashboardBackendResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly API_URL = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  /**
   * Transform backend response to frontend model
   */
  private transformDashboardData(backend: DashboardBackendResponse | null): DashboardStats {
    console.log('🔍 Backend Dashboard Response:', backend);

    if (!backend) {
      console.warn('⚠️ No backend data received, returning zeros');
      return this.getEmptyStats();
    }

    const transformed = {
      // Map backend fields to frontend fields
      // For Admin/Officer: totalLoans, For Customer: myTotalLoans
      totalLoans: this.getNumber(backend.totalLoans) || this.getNumber(backend.myTotalLoans),
      activeLoans: this.getNumber(backend.myActiveLoans) || this.getNumber(backend.disbursedLoans),
      closedLoans: this.getNumber(backend.myClosedLoans),
      // For Admin/Officer: pendingApprovals, For Customer: myPendingLoans
      pendingLoans: this.getNumber(backend.pendingApprovals) || this.getNumber(backend.myPendingLoans),
      underReviewLoans: 0, // Backend doesn't provide this
      approvedLoans: this.getNumber(backend.approvedLoans),
      rejectedLoans: 0, // Backend doesn't provide this
      totalDisbursedAmount: this.getNumber(backend.totalDisbursedAmount),
      overdueEmis: this.getNumber(backend.overdueCount),
      totalOutstanding: this.getNumber(backend.totalOutstanding) || this.getNumber(backend.pendingEmiAmount),
      nextEmiDueDate: backend.nextEmiDueDate,
      nextEmiAmount: this.getNumber(backend.nextEmiAmount),
      nextEmiLoanId: backend.nextEmiLoanId,
      recentApplications: backend.recentLoans || backend.myLoans || [],
      upcomingEmis: []
    };

    console.log('✅ Transformed Dashboard Data:', transformed);
    return transformed;
  }

  /**
   * Safely convert to number, handling BigDecimal and null values
   */
  private getNumber(value: any): number {
    if (value === null || value === undefined) {
      return 0;
    }
    const num = typeof value === 'number' ? value : Number(value);
    return isNaN(num) ? 0 : num;
  }

  /**
   * Get empty stats object
   */
  private getEmptyStats(): DashboardStats {
    return {
      totalLoans: 0,
      activeLoans: 0,
      closedLoans: 0,
      pendingLoans: 0,
      underReviewLoans: 0,
      approvedLoans: 0,
      rejectedLoans: 0,
      totalDisbursedAmount: 0,
      overdueEmis: 0,
      totalOutstanding: 0,
      recentApplications: [],
      upcomingEmis: []
    };
  }

  /**
   * Get role-specific dashboard data
   * Admin sees overall stats, Officers see assigned loans, Customers see their loan summary
   */
  getDashboard(): Observable<ApiResponse<DashboardStats>> {
    console.log('📊 Fetching dashboard from:', this.API_URL);

    return this.http.get<DashboardBackendResponse>(this.API_URL).pipe(
      tap(response => console.log('📡 Raw API Response:', response)),
      map(response => {
        const transformedData = this.transformDashboardData(response);
        return {
          success: true,
          message: 'Dashboard data retrieved successfully',
          data: transformedData,
          timestamp: new Date().toISOString()
        };
      }),
      catchError(error => {
        console.error('❌ Dashboard API Error:', error);
        console.error('Error Status:', error.status);
        console.error('Error Message:', error.message);

        // Return empty stats on error
        return of({
          success: false,
          message: error.error?.message || 'Failed to load dashboard data',
          data: this.getEmptyStats(),
          timestamp: new Date().toISOString()
        });
      })
    );
  }
}
