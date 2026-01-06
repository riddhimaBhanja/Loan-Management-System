import { Component, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { LoanService } from '@core/services/loan.service';
import { AuthService } from '@core/services/auth.service';
import { NotificationService } from '@core/services/notification.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { StatusBadgePipe } from '@shared/pipes/status-badge.pipe';
import { HasAnyRoleDirective } from '@shared/directives/has-any-role.directive';
import { Loan, DisbursementDetails, LoanStatus } from '@core/models/loan.model';
import { ApiResponse } from '@core/models/api-response.model';
import { Document } from '@core/models/document.model';
import { DocumentUploadComponent } from '../document-upload/document-upload.component';
import { DocumentListComponent } from '../document-list/document-list.component';
import { LoanDisburseDialogComponent } from '../loan-disburse-dialog.component';

@Component({
  selector: 'app-loan-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatDividerModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    PageHeaderComponent,
    InrCurrencyPipe,
    StatusBadgePipe,
    HasAnyRoleDirective,
    DocumentUploadComponent,
    DocumentListComponent
  ],
  templateUrl: './loan-detail.component.html',
  styleUrls: ['./loan-detail.component.scss']
})
export class LoanDetailComponent implements OnInit {
  @ViewChild(DocumentListComponent) documentList!: DocumentListComponent;

  loan = signal<Loan | null>(null);
  disbursementDetails = signal<DisbursementDetails | null>(null);
  isLoading = signal<boolean>(true);
  isLoadingDisbursement = signal<boolean>(false);
  isStaff = signal<boolean>(false);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private loanService: LoanService,
    private authService: AuthService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {
    this.isStaff.set(this.authService.hasAnyRole(['ADMIN', 'LOAN_OFFICER']));
  }

  ngOnInit(): void {
    const loanId = this.route.snapshot.params['id'];
    this.loadLoan(loanId);
  }

  loadLoan(id: number): void {
    this.isLoading.set(true);
    this.loanService.getLoanById(id).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.loan.set(response.data);

          // Load disbursement details if loan is disbursed
          if (response.data.status === LoanStatus.DISBURSED || response.data.status === LoanStatus.CLOSED) {
            this.loadDisbursementDetails(id);
          }
        }
        this.isLoading.set(false);
      },
      error: (error) => {
        this.notificationService.error('Failed to load loan details');
        this.isLoading.set(false);
        this.router.navigate(['/loans']);
      }
    });
  }

  loadDisbursementDetails(loanId: number): void {
    this.isLoadingDisbursement.set(true);
    this.loanService.getDisbursementDetails(loanId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.disbursementDetails.set(response.data);
        }
        this.isLoadingDisbursement.set(false);
      },
      error: (error) => {
        // Silently handle 404 errors - disbursement might not exist yet
        // This is expected for loans that haven't been disbursed
        if (error?.status !== 404) {
          console.error('Failed to load disbursement details:', error);
        }
        this.isLoadingDisbursement.set(false);
      }
    });
  }

  markAsUnderReview(): void {
    const loanId = this.loan()?.id;
    if (!loanId) return;

    this.loanService.markUnderReview(loanId).subscribe({
      next: (response: ApiResponse<Loan>) => {
        if (response.success && response.data) {
          this.loan.set(response.data);
          this.notificationService.success('Loan marked as under review');
        }
      },
      error: () => {
        this.notificationService.error('Failed to update loan status');
      }
    });
  }

  navigateToApproval(): void {
    const loanId = this.loan()?.id;
    if (loanId) {
      this.router.navigate(['/loans', loanId, 'review']);
    }
  }

  viewEmiSchedule(): void {
    const loanId = this.loan()?.id;
    if (loanId) {
      this.router.navigate(['/emis', 'loan', loanId]);
    }
  }

  canMarkUnderReview(): boolean {
    const loan = this.loan();
    // Allow marking under review for PENDING or APPLIED status
    return this.isStaff() &&
           (loan?.status === LoanStatus.PENDING || loan?.status === LoanStatus.APPLIED);
  }

  canApproveOrReject(): boolean {
    const loan = this.loan();
    // Allow approval/rejection for PENDING, APPLIED, or UNDER_REVIEW status
    return this.isStaff() &&
           (loan?.status === LoanStatus.PENDING ||
            loan?.status === LoanStatus.APPLIED ||
            loan?.status === LoanStatus.UNDER_REVIEW);
  }

  canDisburse(): boolean {
    const loan = this.loan();
    // Can only disburse if status is APPROVED and not already disbursed
    // Check both status and disbursedAt to handle edge cases
    return this.isStaff() &&
           loan?.status === LoanStatus.APPROVED &&
           !loan?.disbursedAt;
  }

  canViewEmi(): boolean {
    const loan = this.loan();
    return loan?.status === LoanStatus.DISBURSED || loan?.status === LoanStatus.CLOSED;
  }

  disburseLoan(): void {
    const loan = this.loan();
    if (!loan) return;

    const dialogRef = this.dialog.open(LoanDisburseDialogComponent, {
      width: '600px',
      data: { loan }
    });

    dialogRef.afterClosed().subscribe(result => {
      // Always reload loan data when dialog closes
      // This ensures the UI reflects the current state from the backend
      this.loadLoan(loan.id);

      // If disbursement was successful, retry loading disbursement details after a short delay
      // This handles race conditions where the transaction might not be immediately committed
      if (result === true) {
        setTimeout(() => {
          if (this.loan()?.status === LoanStatus.DISBURSED) {
            this.loadDisbursementDetails(loan.id);
          }
        }, 1000);
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: Record<string, string> = {
      'PENDING': 'status-warning',
      'APPLIED': 'status-info',
      'UNDER_REVIEW': 'status-warning',
      'APPROVED': 'status-success',
      'DISBURSED': 'status-success',
      'REJECTED': 'status-danger',
      'CLOSED': 'status-secondary'
    };
    return statusMap[status] || 'status-default';
  }

  onDocumentUploaded(document: Document): void {
    if (this.documentList) {
      this.documentList.onDocumentAdded(document);
    }
  }
  
}

