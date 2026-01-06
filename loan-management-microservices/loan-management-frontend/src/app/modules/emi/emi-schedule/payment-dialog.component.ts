import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmiService } from '@core/services/emi.service';
import { EmiSchedule, PaymentMethod, EmiPaymentRequest } from '@core/models/emi.model';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';

export interface PaymentDialogData {
  emiSchedule: EmiSchedule;
}

@Component({
  selector: 'app-payment-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    InrCurrencyPipe
  ],
  template: `
    <h2 mat-dialog-title>Record EMI Payment</h2>

    <mat-dialog-content>
      <div class="payment-info">
        <div class="info-row">
          <span class="label">EMI Number:</span>
          <span class="value">{{ data.emiSchedule.installmentNumber }}</span>
        </div>
        <div class="info-row">
          <span class="label">Due Date:</span>
          <span class="value">{{ data.emiSchedule.dueDate | date: 'dd MMM yyyy' }}</span>
        </div>
        <div class="info-row">
          <span class="label">EMI Amount:</span>
          <span class="value amount">{{ data.emiSchedule.totalEmi | inrCurrency }}</span>
        </div>
      </div>

      <form [formGroup]="paymentForm" class="payment-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Payment Amount</mat-label>
          <input matInput type="number" formControlName="paymentAmount" placeholder="Enter payment amount" />
          <span matPrefix>₹&nbsp;</span>
          <mat-hint>EMI Amount: {{ data.emiSchedule.totalEmi | inrCurrency }}</mat-hint>
          @if (paymentForm.get('paymentAmount')?.invalid && paymentForm.get('paymentAmount')?.touched) {
            <mat-error>Please enter a valid payment amount (greater than 0)</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Payment Date</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="paymentDate" placeholder="Select payment date" />
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
          @if (paymentForm.get('paymentDate')?.invalid && paymentForm.get('paymentDate')?.touched) {
            <mat-error>Payment date is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Payment Method</mat-label>
          <mat-select formControlName="paymentMethod">
            @for (method of paymentMethods; track method.value) {
              <mat-option [value]="method.value">{{ method.label }}</mat-option>
            }
          </mat-select>
          @if (paymentForm.get('paymentMethod')?.invalid && paymentForm.get('paymentMethod')?.touched) {
            <mat-error>Please select a payment method</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Transaction Reference</mat-label>
          <input matInput formControlName="transactionReference" placeholder="Enter transaction/cheque number" maxlength="100" />
          <mat-hint *ngIf="paymentForm.get('paymentMethod')?.value === 'CASH'">Optional - Transaction ID, Cheque number, etc.</mat-hint>
          <mat-hint *ngIf="paymentForm.get('paymentMethod')?.value && paymentForm.get('paymentMethod')?.value !== 'CASH'">Required - Transaction ID, Cheque/DD number, UTR, etc.</mat-hint>
          @if (paymentForm.get('transactionReference')?.invalid && paymentForm.get('transactionReference')?.touched) {
            <mat-error>Transaction reference is required for this payment method</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Remarks</mat-label>
          <textarea matInput formControlName="remarks" rows="3" placeholder="Additional notes (optional)" maxlength="500"></textarea>
          <mat-hint>{{ paymentForm.get('remarks')?.value?.length || 0 }}/500</mat-hint>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isSubmitting()">Cancel</button>
      <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="paymentForm.invalid || isSubmitting()">
        @if (isSubmitting()) {
          <mat-spinner diameter="20"></mat-spinner>
        } @else {
          <span>Record Payment</span>
        }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .payment-info {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 24px;
      border: 1px solid rgba(102, 126, 234, 0.2);
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;

      &:not(:last-child) {
        border-bottom: 1px solid rgba(0, 0, 0, 0.08);
      }
    }

    .label {
      font-weight: 500;
      color: rgba(0, 0, 0, 0.7);
      font-size: 14px;
    }

    .value {
      font-weight: 600;
      color: rgba(0, 0, 0, 0.87);
      font-size: 15px;

      &.amount {
        color: #667eea;
        font-size: 18px;
      }
    }

    .payment-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .full-width {
      width: 100%;
    }

    mat-dialog-content {
      min-width: 500px;
      max-width: 600px;
      padding: 24px;
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
  `]
})
export class PaymentDialogComponent {
  paymentForm: FormGroup;
  isSubmitting = signal<boolean>(false);

  paymentMethods = [
    { value: PaymentMethod.CASH, label: 'Cash' },
    { value: PaymentMethod.CHEQUE, label: 'Cheque' },
    { value: PaymentMethod.NEFT, label: 'NEFT' },
    { value: PaymentMethod.RTGS, label: 'RTGS' },
    { value: PaymentMethod.UPI, label: 'UPI' },
    { value: PaymentMethod.DEBIT_CARD, label: 'Debit Card' },
    { value: PaymentMethod.CREDIT_CARD, label: 'Credit Card' },
    { value: PaymentMethod.NET_BANKING, label: 'Net Banking' },
    { value: PaymentMethod.DEMAND_DRAFT, label: 'Demand Draft' }
  ];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PaymentDialogComponent>,
    private emiService: EmiService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: PaymentDialogData
  ) {
    this.paymentForm = this.fb.group({
      paymentAmount: [data.emiSchedule.totalEmi, [Validators.required, Validators.min(0.01)]],
      paymentDate: [new Date(), Validators.required],
      paymentMethod: ['', Validators.required],
      transactionReference: ['', Validators.maxLength(100)],
      remarks: ['', Validators.maxLength(500)]
    });

    // Listen to payment method changes to update transaction reference validation
    this.paymentForm.get('paymentMethod')?.valueChanges.subscribe((method: PaymentMethod) => {
      const transactionRefControl = this.paymentForm.get('transactionReference');
      if (method === PaymentMethod.CASH) {
        // Cash doesn't require transaction reference
        transactionRefControl?.clearValidators();
        transactionRefControl?.setValidators([Validators.maxLength(100)]);
      } else {
        // All other methods require transaction reference
        transactionRefControl?.setValidators([Validators.required, Validators.maxLength(100)]);
      }
      transactionRefControl?.updateValueAndValidity();
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.paymentForm.valid) {
      this.isSubmitting.set(true);

      const formValue = this.paymentForm.value;
      const paymentRequest: EmiPaymentRequest = {
        emiScheduleId: this.data.emiSchedule.id,
        amount: formValue.paymentAmount,
        paymentDate: this.formatDate(formValue.paymentDate),
        paymentMethod: formValue.paymentMethod,
        transactionReference: formValue.transactionReference && formValue.transactionReference.trim() !== ''
          ? formValue.transactionReference.trim()
          : (formValue.paymentMethod === PaymentMethod.CASH ? '' : formValue.transactionReference),
        remarks: formValue.remarks || undefined
      };

      this.emiService.recordPayment(paymentRequest).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('Payment recorded successfully', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.dialogRef.close(true);
          }
          this.isSubmitting.set(false);
        },
        error: (error) => {
          console.error('Error recording payment:', error);
          this.snackBar.open(
            error.error?.message || 'Failed to record payment. Please try again.',
            'Close',
            {
              duration: 5000,
              panelClass: ['error-snackbar']
            }
          );
          this.isSubmitting.set(false);
        }
      });
    }
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
