import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { LoanTypeService } from '@core/services/loan-type.service';
import { LoanType, CreateLoanTypeRequest, UpdateLoanTypeRequest } from '@core/models/loan-type.model';

export interface LoanTypeFormDialogData {
  loanType?: LoanType; // If provided, edit mode; otherwise create mode
}

@Component({
  selector: 'app-loan-type-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>{{ isEditMode() ? 'Edit Loan Type' : 'Create New Loan Type' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="loanTypeForm" class="loan-type-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Loan Type Name</mat-label>
          <input matInput formControlName="name" placeholder="e.g., Personal Loan" maxlength="100" />
          @if (loanTypeForm.get('name')?.invalid && loanTypeForm.get('name')?.touched) {
            <mat-error>Loan type name is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="3" placeholder="Describe this loan type" maxlength="500"></textarea>
          @if (loanTypeForm.get('description')?.invalid && loanTypeForm.get('description')?.touched) {
            <mat-error>Description is required</mat-error>
          }
          <mat-hint>{{ loanTypeForm.get('description')?.value?.length || 0 }}/500</mat-hint>
        </mat-form-field>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Minimum Amount</mat-label>
            <input matInput type="number" formControlName="minAmount" placeholder="0" />
            <span matPrefix>₹&nbsp;</span>
            @if (loanTypeForm.get('minAmount')?.invalid && loanTypeForm.get('minAmount')?.touched) {
              <mat-error>Min amount must be greater than 0</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Maximum Amount</mat-label>
            <input matInput type="number" formControlName="maxAmount" placeholder="0" />
            <span matPrefix>₹&nbsp;</span>
            @if (loanTypeForm.get('maxAmount')?.invalid && loanTypeForm.get('maxAmount')?.touched) {
              <mat-error>Max amount must be greater than min amount</mat-error>
            }
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Minimum Tenure (Months)</mat-label>
            <input matInput type="number" formControlName="minTenureMonths" placeholder="1" />
            @if (loanTypeForm.get('minTenureMonths')?.invalid && loanTypeForm.get('minTenureMonths')?.touched) {
              <mat-error>Min tenure must be at least 1 month</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Maximum Tenure (Months)</mat-label>
            <input matInput type="number" formControlName="maxTenureMonths" placeholder="1" />
            @if (loanTypeForm.get('maxTenureMonths')?.invalid && loanTypeForm.get('maxTenureMonths')?.touched) {
              <mat-error>Max tenure must be greater than min tenure</mat-error>
            }
          </mat-form-field>
        </div>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Interest Rate (% per annum)</mat-label>
          <input matInput type="number" formControlName="interestRate" placeholder="12.0" step="0.1" />
          <span matSuffix>%</span>
          @if (loanTypeForm.get('interestRate')?.invalid && loanTypeForm.get('interestRate')?.touched) {
            <mat-error>Interest rate must be between 0.1% and 100%</mat-error>
          }
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isSubmitting()">Cancel</button>
      <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="loanTypeForm.invalid || isSubmitting()">
        @if (isSubmitting()) {
          <mat-spinner diameter="20"></mat-spinner>
        } @else {
          <span>{{ isEditMode() ? 'Update' : 'Create' }}</span>
        }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content {
      min-width: 600px;
      max-width: 700px;
      padding: 24px;
    }

    .loan-type-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .full-width {
      width: 100%;
    }

    .form-row {
      display: flex;
      gap: 16px;
      width: 100%;
    }

    .half-width {
      flex: 1;
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

      .form-row {
        flex-direction: column;
        gap: 8px;
      }
    }
  `]
})
export class LoanTypeFormDialogComponent {
  loanTypeForm: FormGroup;
  isSubmitting = signal<boolean>(false);
  isEditMode = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<LoanTypeFormDialogComponent>,
    private loanTypeService: LoanTypeService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: LoanTypeFormDialogData
  ) {
    this.isEditMode.set(!!data.loanType);
    this.loanTypeForm = this.createForm();

    if (data.loanType) {
      this.prefillForm(data.loanType);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      minAmount: ['', [Validators.required, Validators.min(1)]],
      maxAmount: ['', [Validators.required, Validators.min(1)]],
      minTenureMonths: ['', [Validators.required, Validators.min(1)]],
      maxTenureMonths: ['', [Validators.required, Validators.min(1)]],
      interestRate: ['', [Validators.required, Validators.min(0.1), Validators.max(100)]]
    }, {
      validators: [this.validateAmountRange(), this.validateTenureRange()]
    });
  }

  prefillForm(loanType: LoanType): void {
    this.loanTypeForm.patchValue({
      name: loanType.name,
      description: loanType.description,
      minAmount: loanType.minAmount,
      maxAmount: loanType.maxAmount,
      minTenureMonths: loanType.minTenureMonths,
      maxTenureMonths: loanType.maxTenureMonths,
      interestRate: loanType.interestRate
    });
  }

  validateAmountRange() {
    return (group: FormGroup) => {
      const minAmount = group.get('minAmount')?.value;
      const maxAmount = group.get('maxAmount')?.value;

      if (minAmount && maxAmount && parseFloat(maxAmount) <= parseFloat(minAmount)) {
        group.get('maxAmount')?.setErrors({ invalidRange: true });
        return { invalidAmountRange: true };
      }

      return null;
    };
  }

  validateTenureRange() {
    return (group: FormGroup) => {
      const minTenure = group.get('minTenureMonths')?.value;
      const maxTenure = group.get('maxTenureMonths')?.value;

      if (minTenure && maxTenure && parseInt(maxTenure) <= parseInt(minTenure)) {
        group.get('maxTenureMonths')?.setErrors({ invalidRange: true });
        return { invalidTenureRange: true };
      }

      return null;
    };
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.loanTypeForm.valid) {
      this.isSubmitting.set(true);

      if (this.isEditMode()) {
        this.updateLoanType();
      } else {
        this.createLoanType();
      }
    }
  }

  createLoanType(): void {
    const request: CreateLoanTypeRequest = this.loanTypeForm.value;

    this.loanTypeService.createLoanType(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.snackBar.open('Loan type created successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.dialogRef.close(true);
        }
        this.isSubmitting.set(false);
      },
      error: (error) => {
        console.error('Error creating loan type:', error);

        // Extract error message from various possible structures
        let errorMessage = 'Failed to create loan type. Please try again.';
        if (error.error) {
          if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.error.error && error.error.error.message) {
            errorMessage = error.error.error.message;
          } else if (error.error.message) {
            errorMessage = error.error.message;
          }
        } else if (error.message) {
          errorMessage = error.message;
        }

        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isSubmitting.set(false);
      }
    });
  }

  updateLoanType(): void {
    const loanTypeId = this.data.loanType!.id;
    const request: UpdateLoanTypeRequest = this.loanTypeForm.value;

    this.loanTypeService.updateLoanType(loanTypeId, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.snackBar.open('Loan type updated successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.dialogRef.close(true);
        }
        this.isSubmitting.set(false);
      },
      error: (error) => {
        console.error('Error updating loan type:', error);

        // Extract error message from various possible structures
        let errorMessage = 'Failed to update loan type. Please try again.';
        if (error.error) {
          if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.error.error && error.error.error.message) {
            errorMessage = error.error.error.message;
          } else if (error.error.message) {
            errorMessage = error.error.message;
          }
        } else if (error.message) {
          errorMessage = error.message;
        }

        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isSubmitting.set(false);
      }
    });
  }
}
