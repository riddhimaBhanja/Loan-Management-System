import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EmiService } from '@core/services/emi.service';
import { LoanService } from '@core/services/loan.service';
import { AuthService } from '@core/services/auth.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { StatusBadgePipe } from '@shared/pipes/status-badge.pipe';
import { EmiSchedule, EmiPayment } from '@core/models/emi.model';
import { Loan } from '@core/models/loan.model';
import { PaymentDialogComponent } from './payment-dialog.component';

@Component({
  selector: 'app-emi-schedule',
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
    MatDialogModule,
    MatTooltipModule,
    PageHeaderComponent,
    InrCurrencyPipe,
    StatusBadgePipe
  ],
  templateUrl: './emi-schedule.component.html',
  styleUrls: ['./emi-schedule.component.scss']
})
export class EmiScheduleComponent implements OnInit {
  emiSchedule = signal<EmiSchedule[]>([]);
  paymentHistory = signal<EmiPayment[]>([]);
  loan = signal<Loan | null>(null);
  isLoading = signal<boolean>(true);
  isLoadingPayments = signal<boolean>(false);

  displayedColumns: string[] = [
    'emiNumber',
    'dueDate',
    'principalAmount',
    'interestAmount',
    'totalEmi',
    'principalBalance',
    'status',
    'actions'
  ];

  paymentColumns: string[] = [
    'paymentDate',
    'paymentAmount',
    'paymentMethod',
    'transactionReference',
    'recordedBy'
  ];

  constructor(
    private route: ActivatedRoute,
    private emiService: EmiService,
    private loanService: LoanService,
    private authService: AuthService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const loanId = this.route.snapshot.params['loanId'];
    this.loadLoanDetails(loanId);
    this.loadEmiSchedule(loanId);
    this.loadPaymentHistory(loanId);
  }

  loadLoanDetails(loanId: number): void {
    this.loanService.getLoanById(loanId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.loan.set(response.data);
        }
      }
    });
  }

  loadEmiSchedule(loanId: number): void {
    this.isLoading.set(true);
    this.emiService.getEmiSchedule(loanId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.emiSchedule.set(response.data);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  loadPaymentHistory(loanId: number): void {
    this.isLoadingPayments.set(true);
    this.emiService.getPaymentHistory(loanId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.paymentHistory.set(response.data);
        }
        this.isLoadingPayments.set(false);
      },
      error: () => {
        this.isLoadingPayments.set(false);
      }
    });
  }

  getTotalPaid(): number {
    return this.emiSchedule().filter(emi => emi.status === 'PAID').length;
  }

  getTotalPending(): number {
    return this.emiSchedule().filter(emi => emi.status === 'PENDING').length;
  }

  getTotalOverdue(): number {
    return this.emiSchedule().filter(emi => emi.status === 'OVERDUE').length;
  }

  getTotalAmountPaid(): number {
    return this.emiSchedule()
      .filter(emi => emi.status === 'PAID')
      .reduce((sum, emi) => sum + Number(emi.totalEmi), 0);
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: Record<string, string> = {
      'PENDING': 'status-warning',
      'PAID': 'status-success',
      'OVERDUE': 'status-danger',
      'PARTIAL': 'status-info'
    };
    return statusMap[status] || 'status-default';
  }

  canRecordPayment(): boolean {
    return this.authService.hasRole('ADMIN') || this.authService.hasRole('LOAN_OFFICER');
  }

  canPayEmi(emi: EmiSchedule): boolean {
    return this.canRecordPayment() && (emi.status === 'PENDING' || emi.status === 'OVERDUE');
  }

  openPaymentDialog(emi: EmiSchedule): void {
    const dialogRef = this.dialog.open(PaymentDialogComponent, {
      width: '600px',
      disableClose: true,
      data: { emiSchedule: emi }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Reload the EMI schedule and payment history after successful payment
        const loanId = this.route.snapshot.params['loanId'];
        this.loadEmiSchedule(loanId);
        this.loadPaymentHistory(loanId);
      }
    });
  }

  getPaymentMethodLabel(method: string): string {
    const methodMap: Record<string, string> = {
      'CASH': 'Cash',
      'CHEQUE': 'Cheque',
      'NEFT': 'NEFT',
      'RTGS': 'RTGS',
      'UPI': 'UPI',
      'DEBIT_CARD': 'Debit Card',
      'CREDIT_CARD': 'Credit Card',
      'NET_BANKING': 'Net Banking',
      'DEMAND_DRAFT': 'Demand Draft'
    };
    return methodMap[method] || method;
  }
}
