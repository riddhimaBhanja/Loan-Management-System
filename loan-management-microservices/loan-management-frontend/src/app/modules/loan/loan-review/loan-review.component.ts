import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { LoanService } from '@core/services/loan.service';
import { NotificationService } from '@core/services/notification.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '@shared/components/confirmation-dialog/confirmation-dialog.component';
import { Loan, LoanStatus } from '@core/models/loan.model';

@Component({
  selector: 'app-loan-review',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    PageHeaderComponent,
    InrCurrencyPipe
  ],
  templateUrl: './loan-review.component.html',
  styleUrls: ['./loan-review.component.scss']
})
export class LoanReviewComponent implements OnInit {
  loan = signal<Loan | null>(null);
  approvalForm!: FormGroup;
  rejectionForm!: FormGroup;
  isLoading = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  LoanStatus = LoanStatus; // Expose enum to template

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private loanService: LoanService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    const loanId = this.route.snapshot.params['id'];
    this.loadLoan(loanId);
  }

  initForms(): void {
    this.approvalForm = this.fb.group({
      approvedAmount: ['', [Validators.required, Validators.min(1)]],
      interestRate: ['', [Validators.required, Validators.min(0.1), Validators.max(30)]],
      remarks: ['']
    });

    this.rejectionForm = this.fb.group({
      rejectionReason: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      notes: ['', [Validators.maxLength(500)]]
    });
  }

  loadLoan(id: number): void {
    this.isLoading.set(true);
    this.loanService.getLoanById(id).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.loan.set(response.data);
          this.prefillApprovalForm(response.data);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.notificationService.error('Failed to load loan details');
        this.isLoading.set(false);
        this.router.navigate(['/loans']);
      }
    });
  }

  prefillApprovalForm(loan: Loan): void {
    this.approvalForm.patchValue({
      approvedAmount: loan.requestedAmount,
      interestRate: 12.0,
      remarks: ''
    });
  }

  canApproveOrReject(): boolean {
    const loan = this.loan();
    // Only allow approval/rejection for PENDING, APPLIED, or UNDER_REVIEW status
    return loan?.status === LoanStatus.PENDING ||
           loan?.status === LoanStatus.APPLIED ||
           loan?.status === LoanStatus.UNDER_REVIEW;
  }

  isAlreadyProcessed(): boolean {
    const loan = this.loan();
    return loan?.status === LoanStatus.APPROVED ||
           loan?.status === LoanStatus.REJECTED ||
           loan?.status === LoanStatus.DISBURSED ||
           loan?.status === LoanStatus.CLOSED;
  }

  getProcessedStatusMessage(): string {
    const loan = this.loan();
    if (!loan) return '';

    switch (loan.status) {
      case LoanStatus.APPROVED:
        return 'This loan has already been approved and cannot be modified.';
      case LoanStatus.REJECTED:
        return 'This loan has already been rejected and cannot be modified.';
      case LoanStatus.DISBURSED:
        return 'This loan has been disbursed and cannot be modified.';
      case LoanStatus.CLOSED:
        return 'This loan is closed and cannot be modified.';
      default:
        return '';
    }
  }

  onApprove(): void {
    if (this.approvalForm.invalid) {
      this.approvalForm.markAllAsTouched();
      return;
    }

    if (!this.canApproveOrReject()) {
      this.notificationService.error('This loan cannot be approved in its current status');
      return;
    }

    const dialogData: ConfirmationDialogData = {
      title: 'Approve Loan Application',
      message: 'Are you sure you want to approve this loan application? This action cannot be undone.',
      confirmText: 'Approve',
      confirmColor: 'primary'
    };

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '400px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.submitApproval();
      }
    });
  }

  submitApproval(): void {
    const loanId = this.loan()?.id;
    if (!loanId) return;

    this.isSubmitting.set(true);
    const approvalData = this.approvalForm.value;

    this.loanService.approveLoan(loanId, approvalData).subscribe({
      next: (response) => {
        if (response.success) {
          this.notificationService.success('Loan approved successfully');
          this.router.navigate(['/loans', loanId]);
        }
        this.isSubmitting.set(false);
      },
      error: () => {
        this.notificationService.error('Failed to approve loan');
        this.isSubmitting.set(false);
      }
    });
  }

  onReject(): void {
    if (this.rejectionForm.invalid) {
      this.rejectionForm.markAllAsTouched();
      return;
    }

    if (!this.canApproveOrReject()) {
      this.notificationService.error('This loan cannot be rejected in its current status');
      return;
    }

    const dialogData: ConfirmationDialogData = {
      title: 'Reject Loan Application',
      message: 'Are you sure you want to reject this loan application? This action cannot be undone.',
      confirmText: 'Reject',
      cancelText: 'Cancel',
      confirmColor: 'warn'
    };

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '400px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.submitRejection();
      }
    });
  }

  submitRejection(): void {
    const loanId = this.loan()?.id;
    if (!loanId) return;

    this.isSubmitting.set(true);
    const rejectionData = this.rejectionForm.value;

    this.loanService.rejectLoan(loanId, rejectionData).subscribe({
      next: (response) => {
        if (response.success) {
          this.notificationService.success('Loan rejected');
          this.router.navigate(['/loans', loanId]);
        }
        this.isSubmitting.set(false);
      },
      error: () => {
        this.notificationService.error('Failed to reject loan');
        this.isSubmitting.set(false);
      }
    });
  }
}
