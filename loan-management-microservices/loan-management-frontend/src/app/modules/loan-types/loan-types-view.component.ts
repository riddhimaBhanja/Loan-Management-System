import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { LayoutComponent } from '@shared/components/layout/layout.component';
import { LoanTypeService } from '@core/services/loan-type.service';
import { LoanType } from '@core/models/loan-type.model';
import { EmiCalculatorDialogComponent } from './emi-calculator-dialog.component';

@Component({
  selector: 'app-loan-types-view',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    LayoutComponent
  ],
  template: `
    <app-layout>
      <div class="loan-types-container">
        <div class="page-header">
          <h1>Available Loan Types</h1>
          <p class="subtitle">Explore our loan options and find the perfect fit for your needs</p>
        </div>

        @if (isLoading()) {
          <div class="loading-container">
            <mat-spinner></mat-spinner>
            <p>Loading loan types...</p>
          </div>
        } @else if (errorMessage()) {
          <mat-card class="error-card">
            <mat-icon color="warn">error</mat-icon>
            <p>{{ errorMessage() }}</p>
            <button mat-raised-button color="primary" (click)="loadLoanTypes()">Retry</button>
          </mat-card>
        } @else {
          <div class="loan-types-grid">
            @for (loanType of activeLoanTypes(); track loanType.id) {
              <mat-card class="loan-type-card">
                <mat-card-header>
                  <mat-card-title>
                    <mat-icon class="loan-type-icon">{{ getLoanTypeIcon(loanType.name) }}</mat-icon>
                    {{ loanType.name }}
                  </mat-card-title>
                  <mat-chip class="active-chip">Active</mat-chip>
                </mat-card-header>
                <mat-card-content>
                  <p class="description">{{ loanType.description }}</p>

                  <div class="loan-details">
                    <div class="detail-row">
                      <mat-icon>payments</mat-icon>
                      <div class="detail-content">
                        <span class="label">Loan Amount</span>
                        <span class="value">{{ formatCurrency(loanType.minAmount) }} - {{ formatCurrency(loanType.maxAmount) }}</span>
                      </div>
                    </div>

                    <div class="detail-row">
                      <mat-icon>schedule</mat-icon>
                      <div class="detail-content">
                        <span class="label">Tenure Period</span>
                        <span class="value">{{ loanType.minTenureMonths }} - {{ loanType.maxTenureMonths }} months</span>
                      </div>
                    </div>

                    <div class="detail-row">
                      <mat-icon>percent</mat-icon>
                      <div class="detail-content">
                        <span class="label">Interest Rate</span>
                        <span class="value highlight">{{ loanType.interestRate }}% per annum</span>
                      </div>
                    </div>
                  </div>
                </mat-card-content>
                <mat-card-actions>
                  <button mat-raised-button color="primary" (click)="applyForLoan(loanType)">
                    <mat-icon>add_circle</mat-icon>
                    Apply Now
                  </button>
                  <button mat-button (click)="calculateEmi(loanType)">
                    <mat-icon>calculate</mat-icon>
                    Calculate EMI
                  </button>
                </mat-card-actions>
              </mat-card>
            }
          </div>

          @if (activeLoanTypes().length === 0) {
            <mat-card class="no-data-card">
              <mat-icon>inbox</mat-icon>
              <h3>No Loan Types Available</h3>
              <p>There are currently no active loan types. Please check back later.</p>
            </mat-card>
          }
        }
      </div>
    </app-layout>
  `,
  styles: [`
    .loan-types-container {
      padding: 24px;
      max-width: 1400px;
      margin: 0 auto;
    }

    .page-header {
      margin-bottom: 32px;
      text-align: center;

      h1 {
        font-size: 32px;
        font-weight: 600;
        color: #1976d2;
        margin-bottom: 8px;
      }

      .subtitle {
        font-size: 16px;
        color: rgba(0, 0, 0, 0.6);
      }
    }

    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 64px;
      gap: 16px;
    }

    .error-card, .no-data-card {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 48px;
      gap: 16px;
      text-align: center;

      mat-icon {
        font-size: 64px;
        width: 64px;
        height: 64px;
        opacity: 0.6;
      }
    }

    .loan-types-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: 24px;
      margin-top: 24px;
    }

    .loan-type-card {
      display: flex;
      flex-direction: column;
      transition: transform 0.2s, box-shadow 0.2s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }

      mat-card-header {
        padding: 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;

        mat-card-title {
          display: flex;
          align-items: center;
          gap: 12px;
          font-size: 20px;
          font-weight: 600;
          color: white;
          margin: 0;
        }

        .loan-type-icon {
          font-size: 28px;
          width: 28px;
          height: 28px;
        }

        .active-chip {
          background-color: rgba(255, 255, 255, 0.3);
          color: white;
          font-weight: 500;
        }
      }

      mat-card-content {
        padding: 24px;
        flex-grow: 1;

        .description {
          color: rgba(0, 0, 0, 0.7);
          margin-bottom: 24px;
          line-height: 1.6;
        }

        .loan-details {
          display: flex;
          flex-direction: column;
          gap: 16px;

          .detail-row {
            display: flex;
            align-items: flex-start;
            gap: 12px;

            mat-icon {
              color: #667eea;
              margin-top: 4px;
            }

            .detail-content {
              display: flex;
              flex-direction: column;
              flex-grow: 1;

              .label {
                font-size: 13px;
                color: rgba(0, 0, 0, 0.6);
                margin-bottom: 4px;
              }

              .value {
                font-size: 16px;
                font-weight: 500;
                color: rgba(0, 0, 0, 0.87);

                &.highlight {
                  color: #667eea;
                  font-weight: 600;
                }
              }
            }
          }
        }
      }

      mat-card-actions {
        padding: 16px 24px 24px;
        display: flex;
        gap: 12px;
        border-top: 1px solid rgba(0, 0, 0, 0.08);

        button {
          flex: 1;
        }
      }
    }

    @media (max-width: 768px) {
      .loan-types-grid {
        grid-template-columns: 1fr;
      }

      .loan-type-card mat-card-actions {
        flex-direction: column;
      }
    }
  `]
})
export class LoanTypesViewComponent implements OnInit {
  loanTypes = signal<LoanType[]>([]);
  isLoading = signal(false);
  errorMessage = signal('');

  constructor(
    private loanTypeService: LoanTypeService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadLoanTypes();
  }

  loadLoanTypes(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

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

  activeLoanTypes(): LoanType[] {
    return this.loanTypes().filter(lt => lt.isActive);
  }

  formatCurrency(amount: number): string {
    return `₹${amount.toLocaleString('en-IN')}`;
  }

  getLoanTypeIcon(name: string): string {
    const lowerName = name.toLowerCase();
    if (lowerName.includes('home') || lowerName.includes('housing')) return 'home';
    if (lowerName.includes('car') || lowerName.includes('vehicle') || lowerName.includes('auto')) return 'directions_car';
    if (lowerName.includes('education') || lowerName.includes('student')) return 'school';
    if (lowerName.includes('personal')) return 'person';
    if (lowerName.includes('business')) return 'business';
    if (lowerName.includes('gold')) return 'workspace_premium';
    return 'account_balance';
  }

  applyForLoan(loanType: LoanType): void {
    this.router.navigate(['/loans/apply'], { queryParams: { loanTypeId: loanType.id } });
  }

  calculateEmi(loanType: LoanType): void {
    const dialogRef = this.dialog.open(EmiCalculatorDialogComponent, {
      width: '650px',
      maxWidth: '95vw',
      disableClose: false,
      data: { loanType }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.apply) {
        // User wants to apply for loan with calculated values
        this.router.navigate(['/loans/apply'], {
          queryParams: {
            loanTypeId: loanType.id,
            amount: result.amount,
            tenureMonths: result.tenureMonths
          }
        });
      }
    });
  }
}
