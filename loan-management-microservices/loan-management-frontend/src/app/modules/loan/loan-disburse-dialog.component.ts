import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { LoanService } from '@core/services/loan.service';
import { Loan, LoanDisbursementRequest } from '@core/models/loan.model';

export interface LoanDisburseDialogData {
  loan: Loan;
}

@Component({
  selector: 'app-loan-disburse-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>Disburse Loan</h2>

    <mat-dialog-content>
      <div class="loan-info">
        <div class="info-row">
          <span class="label">Application Number:</span>
          <span class="value">{{ data.loan.applicationNumber }}</span>
        </div>
        <div class="info-row">
          <span class="label">Customer:</span>
          <span class="value">{{ data.loan.customerName }}</span>
        </div>
        <div class="info-row">
          <span class="label">Approved Amount:</span>
          <span class="value">₹{{ data.loan.approvedAmount | number:'1.2-2' }}</span>
        </div>
      </div>

      <form [formGroup]="disburseForm" class="disburse-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Disbursement Date</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="disbursementDate" placeholder="Select date" />
          <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
          @if (disburseForm.get('disbursementDate')?.invalid && disburseForm.get('disbursementDate')?.touched) {
            <mat-error>Disbursement date is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Disbursement Method</mat-label>
          <mat-select formControlName="disbursementMethod" placeholder="Select method">
            <mat-option value="BANK_TRANSFER">Bank Transfer</mat-option>
            <mat-option value="CHEQUE">Cheque</mat-option>
            <mat-option value="CASH">Cash</mat-option>
            <mat-option value="ONLINE_PAYMENT">Online Payment</mat-option>
            <mat-option value="RTGS">RTGS</mat-option>
            <mat-option value="NEFT">NEFT</mat-option>
            <mat-option value="IMPS">IMPS</mat-option>
          </mat-select>
          @if (disburseForm.get('disbursementMethod')?.invalid && disburseForm.get('disbursementMethod')?.touched) {
            <mat-error>Disbursement method is required</mat-error>
          }
        </mat-form-field>

        <!-- Bank Details Section -->
        <div class="section-header" *ngIf="showBankFields()">
          <mat-icon>account_balance</mat-icon>
          <h4>Bank Account Details</h4>
        </div>

        <mat-form-field appearance="outline" class="full-width" *ngIf="showBankFields()">
          <mat-label>Account Number</mat-label>
          <input matInput formControlName="accountNumber" placeholder="Customer's bank account number" maxlength="20" />
          <mat-hint>Enter the customer's bank account number</mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width" *ngIf="showBankFields()">
          <mat-label>Bank Name</mat-label>
          <input matInput formControlName="bankName" placeholder="Name of the bank" maxlength="100" />
          <mat-hint>e.g., HDFC Bank, State Bank of India</mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width" *ngIf="showBankFields()">
          <mat-label>IFSC Code</mat-label>
          <input matInput formControlName="ifscCode" placeholder="Bank IFSC code" maxlength="11" />
          <mat-hint>11-character bank IFSC code</mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Reference Number</mat-label>
          <input matInput formControlName="referenceNumber" placeholder="Transaction/Reference Number" maxlength="100" />
          <mat-hint>Optional - Enter transaction or reference number</mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Remarks</mat-label>
          <textarea matInput formControlName="remarks" rows="3" placeholder="Additional remarks (optional)" maxlength="500"></textarea>
          <mat-hint>{{ disburseForm.get('remarks')?.value?.length || 0 }}/500</mat-hint>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isSubmitting()">Cancel</button>
      <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="disburseForm.invalid || isSubmitting()">
        @if (isSubmitting()) {
          <mat-spinner diameter="20"></mat-spinner>
        } @else {
          <span>Disburse Loan</span>
        }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content {
      min-width: 550px;
      max-width: 650px;
      padding: 24px;
    }

    .loan-info {
      background-color: #f5f5f5;
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 24px;
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
    }

    .info-row:last-child {
      margin-bottom: 0;
    }

    .label {
      font-weight: 500;
      color: rgba(0, 0, 0, 0.6);
    }

    .value {
      font-weight: 600;
      color: rgba(0, 0, 0, 0.87);
    }

    .disburse-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .section-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin: 16px 0 8px 0;
      padding-bottom: 8px;
      border-bottom: 2px solid rgba(102, 126, 234, 0.2);

      mat-icon {
        color: #667eea;
      }

      h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #667eea;
      }
    }

    .full-width {
      width: 100%;
    }

    mat-dialog-actions {
      padding: 16px 24px;
      gap: 12px;
    }

    mat-spinner {
      display: inline-block;
      margin: 0 auto;
    }

    ::ng-deep .mat-mdc-dialog-container {
      border-radius: 16px;
    }

    @media (max-width: 768px) {
      mat-dialog-content {
        min-width: 100%;
        max-width: 100%;
        padding: 16px;
      }

      .loan-info {
        padding: 12px;
      }

      .info-row {
        flex-direction: column;
        gap: 4px;
        margin-bottom: 12px;
      }
    }
  `]
})
export class LoanDisburseDialogComponent {
  disburseForm: FormGroup;
  isSubmitting = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<LoanDisburseDialogComponent>,
    private loanService: LoanService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: LoanDisburseDialogData
  ) {
    this.disburseForm = this.createForm();
  }

  createForm(): FormGroup {
    return this.fb.group({
      disbursementDate: [new Date(), [Validators.required]],
      disbursementMethod: ['', [Validators.required, Validators.maxLength(50)]],
      accountNumber: ['', [Validators.maxLength(20)]],
      bankName: ['', [Validators.maxLength(100)]],
      ifscCode: ['', [Validators.maxLength(11)]],
      referenceNumber: ['', [Validators.maxLength(100)]],
      remarks: ['', [Validators.maxLength(500)]]
    });
  }

  showBankFields(): boolean {
    const method = this.disburseForm.get('disbursementMethod')?.value;
    return ['BANK_TRANSFER', 'RTGS', 'NEFT', 'IMPS', 'ONLINE_PAYMENT'].includes(method);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.disburseForm.valid) {
      this.isSubmitting.set(true);
      this.disburseLoan();
    }
  }

  disburseLoan(): void {
    const formValue = this.disburseForm.value;

    // Format the date to YYYY-MM-DD
    const disbursementDate = new Date(formValue.disbursementDate);
    const formattedDate = disbursementDate.toISOString().split('T')[0];

    const request: LoanDisbursementRequest = {
      disbursementDate: formattedDate,
      disbursementMethod: formValue.disbursementMethod,
      accountNumber: formValue.accountNumber || undefined,
      bankName: formValue.bankName || undefined,
      ifscCode: formValue.ifscCode || undefined,
      referenceNumber: formValue.referenceNumber || undefined,
      remarks: formValue.remarks || undefined
    };

    this.loanService.disburseLoan(this.data.loan.id, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.snackBar.open('Loan disbursed successfully. EMI schedule has been generated.', 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar']
          });
          this.dialogRef.close(true);
        }
        this.isSubmitting.set(false);
      },
      error: (error) => {
        console.error('Error disbursing loan:', error);

        // Extract error message - the error interceptor has already processed it
        const errorMessage = error?.message || 'Failed to disburse loan. Please try again.';

        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isSubmitting.set(false);
      }
    });
  }
}
