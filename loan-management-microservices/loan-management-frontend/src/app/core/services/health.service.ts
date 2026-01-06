import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface HealthCheckResponse {
  status: string;
  timestamp: string;
  message?: string;
  userContext?: {
    userId: string;
    username: string;
    roles: string[];
  };
}

@Injectable({
  providedIn: 'root'
})
export class HealthService {
  private readonly API_URL = `${environment.apiUrl}/health`;

  constructor(private http: HttpClient) {}

  /**
   * Public health check - no authentication required
   * Checks if the loan application service is running
   */
  publicHealthCheck(): Observable<ApiResponse<HealthCheckResponse>> {
    return this.http.get<ApiResponse<HealthCheckResponse>>(`${this.API_URL}/public`);
  }

  /**
   * Authenticated health check - requires authentication
   * Verifies authentication is working and returns current user context
   */
  authenticatedHealthCheck(): Observable<ApiResponse<HealthCheckResponse>> {
    return this.http.get<ApiResponse<HealthCheckResponse>>(`${this.API_URL}/authenticated`);
  }
}
