import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatStepperModule } from '@angular/material/stepper';
import { LayoutComponent } from '@shared/components/layout/layout.component';
import { LoanService } from '@core/services/loan.service';
import { LoanTypeService } from '@core/services/loan-type.service';
import { LoanApplicationRequest, EmploymentStatus } from '@core/models/loan.model';
import { LoanType } from '@core/models/loan-type.model';

@Component({
  selector: 'app-loan-apply',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatStepperModule,
    LayoutComponent
  ],
  templateUrl: './loan-apply.component.html',
  styleUrls: ['./loan-apply.component.scss']
})
export class LoanApplyComponent implements OnInit {
  loanTypeForm!: FormGroup;
  loanDetailsForm!: FormGroup;
  employmentForm!: FormGroup;

  loanTypes = signal<LoanType[]>([]);
  selectedLoanType = signal<LoanType | null>(null);
  isLoading = signal(false);
  isSubmitting = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  employmentStatuses = Object.values(EmploymentStatus);

  constructor(
    private fb: FormBuilder,
    private loanService: LoanService,
    private loanTypeService: LoanTypeService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    this.loadLoanTypes();

    // Check for pre-selected loan type and values from query params
    this.route.queryParams.subscribe(params => {
      if (params['loanTypeId']) {
        const loanTypeId = parseInt(params['loanTypeId']);
        if (!isNaN(loanTypeId)) {
          // Set the loan type after loan types are loaded
          setTimeout(() => {
            this.loanTypeForm.patchValue({ loanTypeId });

            // Pre-fill amount and tenure if provided (from EMI calculator)
            if (params['amount']) {
              const amount = parseFloat(params['amount']);
              if (!isNaN(amount)) {
                this.loanDetailsForm.patchValue({ amount });
              }
            }
            if (params['tenureMonths']) {
              const tenure = parseInt(params['tenureMonths']);
              if (!isNaN(tenure)) {
                this.loanDetailsForm.patchValue({ tenureMonths: tenure });
              }
            }
          }, 500);
        }
      }
    });
  }

  initializeForms(): void {
    this.loanTypeForm = this.fb.group({
      loanTypeId: ['', Validators.required]
    });

    this.loanDetailsForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(1000)]],
      tenureMonths: ['', [Validators.required, Validators.min(1)]],
      purpose: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.employmentForm = this.fb.group({
      employmentStatus: ['', Validators.required],
      monthlyIncome: ['', [Validators.required, Validators.min(1000)]]
    });

    this.loanTypeForm.get('loanTypeId')?.valueChanges.subscribe(loanTypeId => {
      const loanType = this.loanTypes().find(lt => lt.id === loanTypeId);
      this.selectedLoanType.set(loanType || null);
      if (loanType) {
        this.updateValidators(loanType);
      }
    });
  }

  updateValidators(loanType: LoanType): void {
    const amountControl = this.loanDetailsForm.get('amount');
    const tenureControl = this.loanDetailsForm.get('tenureMonths');

    amountControl?.setValidators([
      Validators.required,
      Validators.min(loanType.minAmount),
      Validators.max(loanType.maxAmount)
    ]);

    tenureControl?.setValidators([
      Validators.required,
      Validators.min(loanType.minTenureMonths),
      Validators.max(loanType.maxTenureMonths)
    ]);

    amountControl?.updateValueAndValidity();
    tenureControl?.updateValueAndValidity();
  }

  loadLoanTypes(): void {
    this.isLoading.set(true);
    this.loanTypeService.getActiveLoanTypes().subscribe({
      next: (response) => {
        this.isLoading.set(false);
        if (response.success) {
          this.loanTypes.set(response.data);
        }
      },
      error: (error) => {
        this.isLoading.set(false);
        this.errorMessage.set(error.message || 'Failed to load loan types');
      }
    });
  }

  onSubmit(): void {
    if (this.loanTypeForm.invalid || this.loanDetailsForm.invalid || this.employmentForm.invalid) {
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const request: LoanApplicationRequest = {
      ...this.loanTypeForm.value,
      ...this.loanDetailsForm.value,
      ...this.employmentForm.value
    };

    this.loanService.submitLoanApplication(request).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        if (response.success) {
          this.successMessage.set('Loan application submitted successfully!');
          setTimeout(() => {
            this.router.navigate(['/loans/my-loans']);
          }, 2000);
        }
      },
      error: (error) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.message || 'Failed to submit loan application');
      }
    });
  }

  getEstimatedEmi(): number {
    const loanType = this.selectedLoanType();
    const amount = this.loanDetailsForm.get('amount')?.value;
    const tenure = this.loanDetailsForm.get('tenureMonths')?.value;

    if (!loanType || !amount || !tenure) {
      return 0;
    }

    const monthlyRate = loanType.interestRate / 12 / 100;
    const power = Math.pow(1 + monthlyRate, tenure);
    const emi = (amount * monthlyRate * power) / (power - 1);

    return Math.round(emi);
  }

  formatCurrency(amount: number): string {
    return `₹${amount.toLocaleString('en-IN')}`;
  }

  getEmploymentStatusLabel(status: EmploymentStatus): string {
    const labels: Record<EmploymentStatus, string> = {
      [EmploymentStatus.SALARIED]: 'Salaried',
      [EmploymentStatus.SELF_EMPLOYED]: 'Self Employed',
      [EmploymentStatus.BUSINESS_OWNER]: 'Business Owner',
      [EmploymentStatus.UNEMPLOYED]: 'Unemployed',
      [EmploymentStatus.RETIRED]: 'Retired'
    };
    return labels[status] || status;
  }
}
