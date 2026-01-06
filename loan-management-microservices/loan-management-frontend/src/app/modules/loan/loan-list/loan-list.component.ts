import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { LoanService } from '@core/services/loan.service';
import { AuthService } from '@core/services/auth.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { StatusBadgePipe } from '@shared/pipes/status-badge.pipe';
import { HasAnyRoleDirective } from '@shared/directives/has-any-role.directive';
import { Loan, LoanStatus } from '@core/models/loan.model';

@Component({
  selector: 'app-loan-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    PageHeaderComponent,
    InrCurrencyPipe,
    StatusBadgePipe,
    HasAnyRoleDirective
  ],
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {
  loans = signal<Loan[]>([]);
  totalElements = signal<number>(0);
  isLoading = signal<boolean>(false);
  isStaff = signal<boolean>(false);

  displayedColumns: string[] = ['applicationNumber', 'customerName', 'loanType', 'requestedAmount', 'status', 'appliedAt', 'actions'];

  pageSize = 10;
  pageIndex = 0;
  selectedStatus: LoanStatus | '' = '';
  showPendingOnly = signal<boolean>(false);

  statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'APPLIED', label: 'Applied' },
    { value: 'UNDER_REVIEW', label: 'Under Review' },
    { value: 'APPROVED', label: 'Approved' },
    { value: 'REJECTED', label: 'Rejected' },
    { value: 'CLOSED', label: 'Closed' }
  ];

  constructor(
    private loanService: LoanService,
    private authService: AuthService
  ) {
    this.isStaff.set(this.authService.hasAnyRole(['ADMIN', 'LOAN_OFFICER']));
  }

  ngOnInit(): void {
    this.loadLoans();
  }

  loadLoans(): void {
    this.isLoading.set(true);

    if (this.isStaff() && this.showPendingOnly()) {
      // Load pending loans only (no pagination for this endpoint)
      this.loanService.getPendingLoans().subscribe({
        next: (response: any) => {
          if (response.success && response.data) {
            this.loans.set(response.data);
            this.totalElements.set(response.data.length);
          }
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        }
      });
    } else if (this.isStaff()) {
      // Load all loans with filtering and pagination
      this.loanService.getAllLoans(
        this.pageIndex,
        this.pageSize,
        this.selectedStatus ? this.selectedStatus as LoanStatus : undefined
      ).subscribe({
        next: (response: any) => {
          if (response.success && response.data) {
            // Handle both paginated and non-paginated responses
            if (Array.isArray(response.data)) {
              // Backend returns a list directly
              this.loans.set(response.data);
              this.totalElements.set(response.data.length);
            } else if (response.data.content) {
              // Backend returns paginated response
              this.loans.set(response.data.content);
              this.totalElements.set(response.data.totalElements);
            }
          }
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        }
      });
    } else {
      // Customer view - load their own loans
      this.loanService.getMyLoans(this.pageIndex, this.pageSize).subscribe({
        next: (response: any) => {
          if (response.success && response.data) {
            // Handle both paginated and non-paginated responses
            if (Array.isArray(response.data)) {
              // Backend returns a list directly
              this.loans.set(response.data);
              this.totalElements.set(response.data.length);
            } else if (response.data.content) {
              // Backend returns paginated response
              this.loans.set(response.data.content);
              this.totalElements.set(response.data.totalElements);
            }
          }
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        }
      });
    }
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadLoans();
  }

  onStatusChange(): void {
    this.pageIndex = 0;
    this.loadLoans();
  }

  togglePendingOnly(): void {
    this.showPendingOnly.set(!this.showPendingOnly());
    this.pageIndex = 0;
    // Reset status filter when showing pending only
    if (this.showPendingOnly()) {
      this.selectedStatus = '';
    }
    this.loadLoans();
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: Record<string, string> = {
      'APPLIED': 'status-info',
      'UNDER_REVIEW': 'status-warning',
      'APPROVED': 'status-success',
      'REJECTED': 'status-danger',
      'CLOSED': 'status-secondary'
    };
    return statusMap[status] || 'status-default';
  }
}
