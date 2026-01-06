import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EmiService } from '@core/services/emi.service';
import { LoanService } from '@core/services/loan.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { HasAnyRoleDirective } from '@shared/directives/has-any-role.directive';
import { EmiSchedule } from '@core/models/emi.model';
import { Loan } from '@core/models/loan.model';

interface OverdueEmiWithLoan extends EmiSchedule {
  loan?: Loan;
  daysOverdue?: number;
}

@Component({
  selector: 'app-overdue-emis',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDialogModule,
    PageHeaderComponent,
    InrCurrencyPipe,
    HasAnyRoleDirective
  ],
  templateUrl: './overdue-emis.component.html',
  styleUrls: ['./overdue-emis.component.scss']
})
export class OverdueEmisComponent implements OnInit {
  overdueEmis = signal<OverdueEmiWithLoan[]>([]);
  isLoading = signal<boolean>(true);

  displayedColumns: string[] = [
    'loanId',
    'customerName',
    'installmentNumber',
    'dueDate',
    'daysOverdue',
    'totalEmi',
    'outstandingBalance',
    'actions'
  ];

  constructor(
    private emiService: EmiService,
    private loanService: LoanService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadOverdueEmis();
  }

  loadOverdueEmis(): void {
    this.isLoading.set(true);
    this.emiService.getOverdueEmis().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          const emis = response.data.map(emi => ({
            ...emi,
            daysOverdue: this.calculateDaysOverdue(emi.dueDate)
          }));

          // Sort by days overdue (most overdue first)
          emis.sort((a, b) => (b.daysOverdue || 0) - (a.daysOverdue || 0));

          this.overdueEmis.set(emis);

          // Load loan details for each EMI
          this.loadLoanDetails(emis);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  loadLoanDetails(emis: OverdueEmiWithLoan[]): void {
    const uniqueLoanIds = [...new Set(emis.map(emi => emi.loanId))];

    uniqueLoanIds.forEach(loanId => {
      this.loanService.getLoanById(loanId).subscribe({
        next: (response) => {
          if (response.success && response.data) {
            const updatedEmis = this.overdueEmis().map(emi =>
              emi.loanId === loanId ? { ...emi, loan: response.data } : emi
            );
            this.overdueEmis.set(updatedEmis);
          }
        }
      });
    });
  }

  calculateDaysOverdue(dueDate: string): number {
    const due = new Date(dueDate);
    const today = new Date();
    const diff = today.getTime() - due.getTime();
    return Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)));
  }

  getTotalOverdueAmount(): number {
    return this.overdueEmis().reduce((sum, emi) => sum + Number(emi.totalEmi), 0);
  }

  getTotalOutstandingBalance(): number {
    return this.overdueEmis().reduce((sum, emi) => sum + Number(emi.outstandingBalance), 0);
  }

  getUniqueCustomers(): number {
    const customerIds = new Set(this.overdueEmis().map(emi => emi.loan?.customerId).filter(Boolean));
    return customerIds.size;
  }

  getSeverityClass(daysOverdue: number | undefined): string {
    if (!daysOverdue) return 'low';
    if (daysOverdue > 90) return 'critical';
    if (daysOverdue > 60) return 'high';
    if (daysOverdue > 30) return 'medium';
    return 'low';
  }

  getSeverityLabel(daysOverdue: number | undefined): string {
    if (!daysOverdue) return 'Recent';
    if (daysOverdue > 90) return 'Critical';
    if (daysOverdue > 60) return 'High Risk';
    if (daysOverdue > 30) return 'Moderate';
    return 'Recent';
  }
}
