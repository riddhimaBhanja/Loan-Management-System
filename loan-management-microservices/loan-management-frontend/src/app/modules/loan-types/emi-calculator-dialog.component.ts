import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { LoanType } from '@core/models/loan-type.model';

export interface EmiCalculatorDialogData {
  loanType: LoanType;
}

@Component({
  selector: 'app-emi-calculator-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDividerModule
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>calculate</mat-icon>
      EMI Calculator - {{ data.loanType.name }}
    </h2>

    <mat-dialog-content>
      <form [formGroup]="calculatorForm" class="calculator-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Loan Amount</mat-label>
          <input matInput type="number" formControlName="amount" placeholder="Enter loan amount" />
          <span matPrefix>₹&nbsp;</span>
          <mat-hint>Min: {{ formatCurrency(data.loanType.minAmount) }}, Max: {{ formatCurrency(data.loanType.maxAmount) }}</mat-hint>
          @if (calculatorForm.get('amount')?.invalid && calculatorForm.get('amount')?.touched) {
            <mat-error>
              @if (calculatorForm.get('amount')?.errors?.['required']) {
                Amount is required
              }
              @if (calculatorForm.get('amount')?.errors?.['min']) {
                Minimum amount is {{ formatCurrency(data.loanType.minAmount) }}
              }
              @if (calculatorForm.get('amount')?.errors?.['max']) {
                Maximum amount is {{ formatCurrency(data.loanType.maxAmount) }}
              }
            </mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Loan Tenure</mat-label>
          <input matInput type="number" formControlName="tenureMonths" placeholder="Enter tenure" />
          <span matSuffix>&nbsp;months</span>
          <mat-hint>Min: {{ data.loanType.minTenureMonths }}, Max: {{ data.loanType.maxTenureMonths }} months</mat-hint>
          @if (calculatorForm.get('tenureMonths')?.invalid && calculatorForm.get('tenureMonths')?.touched) {
            <mat-error>
              @if (calculatorForm.get('tenureMonths')?.errors?.['required']) {
                Tenure is required
              }
              @if (calculatorForm.get('tenureMonths')?.errors?.['min']) {
                Minimum tenure is {{ data.loanType.minTenureMonths }} months
              }
              @if (calculatorForm.get('tenureMonths')?.errors?.['max']) {
                Maximum tenure is {{ data.loanType.maxTenureMonths }} months
              }
            </mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Interest Rate</mat-label>
          <input matInput type="number" [value]="data.loanType.interestRate" readonly />
          <span matSuffix>&nbsp;% per annum</span>
        </mat-form-field>

        <button mat-raised-button color="primary" type="button" (click)="calculateEmi()"
                [disabled]="calculatorForm.invalid" class="calculate-btn">
          <mat-icon>calculate</mat-icon>
          Calculate EMI
        </button>
      </form>

      @if (emiResult) {
        <mat-divider class="divider"></mat-divider>

        <div class="result-section">
          <h3 class="result-title">
            <mat-icon>assessment</mat-icon>
            EMI Calculation Results
          </h3>

          <div class="result-card highlight-card">
            <div class="result-item">
              <mat-icon class="result-icon">payments</mat-icon>
              <div class="result-content">
                <span class="result-label">Monthly EMI</span>
                <span class="result-value primary">{{ formatCurrency(emiResult.monthlyEmi) }}</span>
              </div>
            </div>
          </div>

          <div class="result-grid">
            <div class="result-card">
              <div class="result-item">
                <mat-icon class="result-icon">account_balance</mat-icon>
                <div class="result-content">
                  <span class="result-label">Principal Amount</span>
                  <span class="result-value">{{ formatCurrency(emiResult.principalAmount) }}</span>
                </div>
              </div>
            </div>

            <div class="result-card">
              <div class="result-item">
                <mat-icon class="result-icon">trending_up</mat-icon>
                <div class="result-content">
                  <span class="result-label">Total Interest</span>
                  <span class="result-value">{{ formatCurrency(emiResult.totalInterest) }}</span>
                </div>
              </div>
            </div>

            <div class="result-card">
              <div class="result-item">
                <mat-icon class="result-icon">receipt_long</mat-icon>
                <div class="result-content">
                  <span class="result-label">Total Amount Payable</span>
                  <span class="result-value">{{ formatCurrency(emiResult.totalAmount) }}</span>
                </div>
              </div>
            </div>

            <div class="result-card">
              <div class="result-item">
                <mat-icon class="result-icon">schedule</mat-icon>
                <div class="result-content">
                  <span class="result-label">Loan Tenure</span>
                  <span class="result-value">{{ emiResult.tenureMonths }} months ({{ emiResult.tenureYears }} years)</span>
                </div>
              </div>
            </div>
          </div>

          <div class="info-box">
            <mat-icon>info</mat-icon>
            <p>This is an approximate EMI calculation. Actual EMI may vary based on the loan approval amount and other factors.</p>
          </div>
        </div>
      }
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Close</button>
      @if (emiResult) {
        <button mat-raised-button color="primary" (click)="onApply()">
          <mat-icon>add_circle</mat-icon>
          Apply for This Loan
        </button>
      }
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content {
      min-width: 500px;
      max-width: 600px;
      padding: 24px;
      max-height: 80vh;
      overflow-y: auto;
    }

    h2[mat-dialog-title] {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #667eea;
      margin: 0;
      padding: 20px 24px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
      border-bottom: 2px solid #667eea;
    }

    .calculator-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .full-width {
      width: 100%;
    }

    .calculate-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
      font-weight: 500;
      margin-top: 8px;
    }

    .divider {
      margin: 32px 0;
    }

    .result-section {
      animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .result-title {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 20px;
      font-weight: 600;
      color: #667eea;
      margin-bottom: 20px;
    }

    .result-card {
      background: #f8f9fa;
      border-radius: 12px;
      padding: 20px;
      border: 1px solid rgba(0, 0, 0, 0.08);

      &.highlight-card {
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
        border: 2px solid #667eea;
        margin-bottom: 16px;
      }
    }

    .result-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      margin-bottom: 20px;
    }

    .result-item {
      display: flex;
      align-items: center;
      gap: 16px;

      .result-icon {
        color: #667eea;
        font-size: 32px;
        width: 32px;
        height: 32px;
      }

      .result-content {
        display: flex;
        flex-direction: column;
        flex: 1;

        .result-label {
          font-size: 13px;
          color: rgba(0, 0, 0, 0.6);
          margin-bottom: 4px;
        }

        .result-value {
          font-size: 18px;
          font-weight: 600;
          color: rgba(0, 0, 0, 0.87);

          &.primary {
            font-size: 28px;
            color: #667eea;
          }
        }
      }
    }

    .info-box {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 16px;
      background: rgba(255, 152, 0, 0.1);
      border-left: 4px solid #ff9800;
      border-radius: 4px;

      mat-icon {
        color: #ff9800;
        margin-top: 2px;
      }

      p {
        margin: 0;
        font-size: 14px;
        line-height: 1.6;
        color: rgba(0, 0, 0, 0.7);
      }
    }

    mat-dialog-actions {
      padding: 16px 24px;
      gap: 12px;
      border-top: 1px solid rgba(0, 0, 0, 0.08);
    }

    @media (max-width: 768px) {
      mat-dialog-content {
        min-width: 100%;
        max-width: 100%;
      }

      .result-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class EmiCalculatorDialogComponent implements OnInit {
  calculatorForm!: FormGroup;
  emiResult: any = null;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EmiCalculatorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EmiCalculatorDialogData
  ) {}

  ngOnInit(): void {
    this.calculatorForm = this.fb.group({
      amount: [
        (this.data.loanType.minAmount + this.data.loanType.maxAmount) / 2,
        [
          Validators.required,
          Validators.min(this.data.loanType.minAmount),
          Validators.max(this.data.loanType.maxAmount)
        ]
      ],
      tenureMonths: [
        Math.floor((this.data.loanType.minTenureMonths + this.data.loanType.maxTenureMonths) / 2),
        [
          Validators.required,
          Validators.min(this.data.loanType.minTenureMonths),
          Validators.max(this.data.loanType.maxTenureMonths)
        ]
      ]
    });

    // Auto-calculate on form init
    if (this.calculatorForm.valid) {
      this.calculateEmi();
    }

    // Auto-calculate on value changes
    this.calculatorForm.valueChanges.subscribe(() => {
      if (this.calculatorForm.valid) {
        this.calculateEmi();
      }
    });
  }

  calculateEmi(): void {
    if (this.calculatorForm.invalid) {
      return;
    }

    const amount = this.calculatorForm.get('amount')?.value;
    const tenureMonths = this.calculatorForm.get('tenureMonths')?.value;
    const annualRate = this.data.loanType.interestRate;

    // Calculate EMI using the formula: EMI = [P x R x (1+R)^N] / [(1+R)^N-1]
    const monthlyRate = annualRate / 12 / 100;
    const emi = amount * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths) /
                (Math.pow(1 + monthlyRate, tenureMonths) - 1);

    const totalAmount = emi * tenureMonths;
    const totalInterest = totalAmount - amount;

    this.emiResult = {
      monthlyEmi: Math.round(emi),
      principalAmount: amount,
      totalInterest: Math.round(totalInterest),
      totalAmount: Math.round(totalAmount),
      tenureMonths: tenureMonths,
      tenureYears: (tenureMonths / 12).toFixed(1)
    };
  }

  formatCurrency(amount: number): string {
    return `₹${amount.toLocaleString('en-IN')}`;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onApply(): void {
    this.dialogRef.close({
      apply: true,
      amount: this.calculatorForm.get('amount')?.value,
      tenureMonths: this.calculatorForm.get('tenureMonths')?.value
    });
  }
}
