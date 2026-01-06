import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { LoanType, CreateLoanTypeRequest, UpdateLoanTypeRequest } from '../models/loan-type.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class LoanTypeService {
  private readonly API_URL = `${environment.apiUrl}/loan-types`;

  constructor(private http: HttpClient) {}

  getAllLoanTypes(): Observable<ApiResponse<LoanType[]>> {
    return this.http.get<ApiResponse<LoanType[]>>(this.API_URL);
  }

  getActiveLoanTypes(): Observable<ApiResponse<LoanType[]>> {
    return this.http.get<ApiResponse<LoanType[]>>(`${this.API_URL}/active`);
  }

  getLoanTypeById(loanTypeId: number): Observable<ApiResponse<LoanType>> {
    return this.http.get<ApiResponse<LoanType>>(`${this.API_URL}/${loanTypeId}`);
  }

  createLoanType(request: CreateLoanTypeRequest): Observable<ApiResponse<LoanType>> {
    return this.http.post<ApiResponse<LoanType>>(this.API_URL, request);
  }

  updateLoanType(loanTypeId: number, request: UpdateLoanTypeRequest): Observable<ApiResponse<LoanType>> {
    return this.http.put<ApiResponse<LoanType>>(`${this.API_URL}/${loanTypeId}`, request);
  }

  deleteLoanType(loanTypeId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${loanTypeId}`);
  }
}
