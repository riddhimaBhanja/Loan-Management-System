import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmiService } from '@core/services/emi.service';
import { AuthService } from '@core/services/auth.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { EmiSchedule } from '@core/models/emi.model';
import { PaymentDialogComponent } from '../emi-schedule/payment-dialog.component';

interface GroupedEmiSchedule {
  loanId: number;
  loanApplicationNumber: string;
  emis: EmiSchedule[];
  totalAmount: number;
  paidAmount: number;
  pendingAmount: number;
  nextDueDate?: string;
}

@Component({
  selector: 'app-my-emi-schedule',
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
    MatTabsModule,
    MatTooltipModule,
    MatDialogModule,
    MatSnackBarModule,
    PageHeaderComponent,
    InrCurrencyPipe,
    PaymentDialogComponent
  ],
  templateUrl: './my-emi-schedule.component.html',
  styleUrls: ['./my-emi-schedule.component.scss']
})
export class MyEmiScheduleComponent implements OnInit {
  allEmis = signal<EmiSchedule[]>([]);
  groupedEmis = signal<GroupedEmiSchedule[]>([]);
  pendingEmis = signal<EmiSchedule[]>([]);
  isLoading = signal<boolean>(true);
  selectedView = signal<'all' | 'pending'>('all');

  displayedColumns: string[] = [
    'installmentNumber',
    'dueDate',
    'principalAmount',
    'interestAmount',
    'totalEmi',
    'outstandingBalance',
    'status',
    'actions'
  ];

  constructor(
    private emiService: EmiService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadMyEmiSchedule();
    this.loadPendingEmis();
  }

  loadMyEmiSchedule(): void {
    this.isLoading.set(true);
    this.emiService.getMyEmiSchedule().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.allEmis.set(response.data);
          this.groupEmisByLoan(response.data);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  loadPendingEmis(): void {
    this.emiService.getPendingEmis().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.pendingEmis.set(response.data);
        }
      }
    });
  }

  groupEmisByLoan(emis: EmiSchedule[]): void {
    const grouped = emis.reduce((acc, emi) => {
      const existing = acc.find(g => g.loanId === emi.loanId);

      if (existing) {
        existing.emis.push(emi);
        existing.totalAmount += emi.totalEmi;
        if (emi.status === 'PAID') {
          existing.paidAmount += emi.totalEmi;
        } else {
          existing.pendingAmount += emi.totalEmi;
          if (!existing.nextDueDate || new Date(emi.dueDate) < new Date(existing.nextDueDate)) {
            existing.nextDueDate = emi.dueDate;
          }
        }
      } else {
        acc.push({
          loanId: emi.loanId,
          loanApplicationNumber: `LOAN-${emi.loanId}`, // This should come from the API
          emis: [emi],
          totalAmount: emi.totalEmi,
          paidAmount: emi.status === 'PAID' ? emi.totalEmi : 0,
          pendingAmount: emi.status !== 'PAID' ? emi.totalEmi : 0,
          nextDueDate: emi.status !== 'PAID' ? emi.dueDate : undefined
        });
      }

      return acc;
    }, [] as GroupedEmiSchedule[]);

    // Sort EMIs within each group by installment number
    grouped.forEach(group => {
      group.emis.sort((a, b) => a.installmentNumber - b.installmentNumber);
    });

    this.groupedEmis.set(grouped);
  }

  setView(view: 'all' | 'pending'): void {
    this.selectedView.set(view);
  }

  getStatusClass(status: string): string {
    const statusMap: Record<string, string> = {
      'PENDING': 'status-pending',
      'PAID': 'status-paid',
      'OVERDUE': 'status-overdue',
      'PARTIALLY_PAID': 'status-partial'
    };
    return statusMap[status] || 'status-default';
  }

  getStatusLabel(status: string): string {
    const labelMap: Record<string, string> = {
      'PENDING': 'Pending',
      'PAID': 'Paid',
      'OVERDUE': 'Overdue',
      'PARTIALLY_PAID': 'Partially Paid'
    };
    return labelMap[status] || status;
  }

  isOverdue(emi: EmiSchedule): boolean {
    return emi.status === 'PENDING' && new Date(emi.dueDate) < new Date();
  }

  getTotalStats() {
    const grouped = this.groupedEmis();
    return {
      totalLoans: grouped.length,
      totalAmount: grouped.reduce((sum, g) => sum + g.totalAmount, 0),
      paidAmount: grouped.reduce((sum, g) => sum + g.paidAmount, 0),
      pendingAmount: grouped.reduce((sum, g) => sum + g.pendingAmount, 0),
      totalPending: this.pendingEmis().length
    };
  }

  /**
   * Open payment dialog for an EMI
   */
  openPaymentDialog(emi: EmiSchedule): void {
    // Don't allow payment for already paid EMIs
    if (emi.status === 'PAID') {
      this.snackBar.open('This EMI has already been paid', 'Close', {
        duration: 3000,
        panelClass: ['info-snackbar']
      });
      return;
    }

    const dialogRef = this.dialog.open(PaymentDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      data: { emiSchedule: emi },
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        // Payment was successful, reload data
        this.loadMyEmiSchedule();
        this.loadPendingEmis();
      }
    });
  }

  /**
   * Check if an EMI can be paid
   */
  canPayEmi(emi: EmiSchedule): boolean {
    return emi.status !== 'PAID';
  }
}
