import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { ReportsService, LoanSummaryReport, EmiCollectionReport } from '@core/services/reports.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-reports-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatTableModule,
    MatSnackBarModule,
    PageHeaderComponent,
    InrCurrencyPipe
  ],
  templateUrl: './reports-dashboard.component.html',
  styleUrls: ['./reports-dashboard.component.scss']
})
export class ReportsDashboardComponent implements OnInit {
  dateRangeForm: FormGroup;
  loanReport = signal<LoanSummaryReport | null>(null);
  emiReport = signal<EmiCollectionReport | null>(null);
  isLoadingLoanReport = signal<boolean>(false);
  isLoadingEmiReport = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private reportsService: ReportsService,
    private snackBar: MatSnackBar
  ) {
    this.dateRangeForm = this.createForm();
  }

  ngOnInit(): void {
    // Load current month reports by default
    this.loadDefaultReports();
  }

  createForm(): FormGroup {
    const today = new Date();
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    return this.fb.group({
      startDate: [firstDayOfMonth, Validators.required],
      endDate: [lastDayOfMonth, Validators.required]
    });
  }

  loadDefaultReports(): void {
    this.loadLoanReport();
    this.loadEmiReport();
  }

  loadLoanReport(): void {
    if (this.dateRangeForm.invalid) {
      this.snackBar.open('Please select valid date range', 'Close', { duration: 3000 });
      return;
    }

    const { startDate, endDate } = this.dateRangeForm.value;
    const startDateStr = this.formatDate(startDate);
    const endDateStr = this.formatDate(endDate);

    this.isLoadingLoanReport.set(true);
    this.reportsService.getLoanSummaryReport(startDateStr, endDateStr).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.loanReport.set(response.data);
        }
        this.isLoadingLoanReport.set(false);
      },
      error: (error) => {
        console.error('Error loading loan report:', error);
        this.snackBar.open('Failed to load loan report', 'Close', { duration: 3000 });
        this.isLoadingLoanReport.set(false);
      }
    });
  }

  loadEmiReport(): void {
    if (this.dateRangeForm.invalid) {
      this.snackBar.open('Please select valid date range', 'Close', { duration: 3000 });
      return;
    }

    const { startDate, endDate } = this.dateRangeForm.value;
    const startDateStr = this.formatDate(startDate);
    const endDateStr = this.formatDate(endDate);

    this.isLoadingEmiReport.set(true);
    this.reportsService.getEmiCollectionReport(startDateStr, endDateStr).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.emiReport.set(response.data);
        }
        this.isLoadingEmiReport.set(false);
      },
      error: (error) => {
        console.error('Error loading EMI report:', error);
        this.snackBar.open('Failed to load EMI report', 'Close', { duration: 3000 });
        this.isLoadingEmiReport.set(false);
      }
    });
  }

  loadAllReports(): void {
    this.loadLoanReport();
    this.loadEmiReport();
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  setQuickRange(range: 'today' | 'week' | 'month' | 'quarter' | 'year'): void {
    const today = new Date();
    let startDate: Date;
    let endDate: Date = today;

    switch (range) {
      case 'today':
        startDate = today;
        break;
      case 'week':
        startDate = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case 'month':
        startDate = new Date(today.getFullYear(), today.getMonth(), 1);
        endDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
        break;
      case 'quarter':
        const quarter = Math.floor(today.getMonth() / 3);
        startDate = new Date(today.getFullYear(), quarter * 3, 1);
        endDate = new Date(today.getFullYear(), quarter * 3 + 3, 0);
        break;
      case 'year':
        startDate = new Date(today.getFullYear(), 0, 1);
        endDate = new Date(today.getFullYear(), 11, 31);
        break;
      default:
        startDate = today;
    }

    this.dateRangeForm.patchValue({ startDate, endDate });
    this.loadAllReports();
  }

  calculateApprovalRate(): number {
    const report = this.loanReport();
    if (!report || report.totalLoans === 0) return 0;
    return (report.approvedLoans / report.totalLoans) * 100;
  }

  calculateRejectionRate(): number {
    const report = this.loanReport();
    if (!report || report.totalLoans === 0) return 0;
    return (report.rejectedLoans / report.totalLoans) * 100;
  }

  calculateDisbursementRate(): number {
    const report = this.loanReport();
    if (!report || report.approvedLoans === 0) return 0;
    return (report.disbursedLoans / report.approvedLoans) * 100;
  }

  exportLoanReport(): void {
    const report = this.loanReport();
    if (!report) return;

    const data = [
      ['Loan Summary Report'],
      ['Period', `${report.startDate} to ${report.endDate}`],
      [''],
      ['Metric', 'Count', 'Amount'],
      ['Total Loans', report.totalLoans, this.formatCurrency(report.totalAmount)],
      ['Approved Loans', report.approvedLoans, this.formatCurrency(report.approvedAmount)],
      ['Rejected Loans', report.rejectedLoans, '-'],
      ['Pending Loans', report.pendingLoans, '-'],
      ['Disbursed Loans', report.disbursedLoans, this.formatCurrency(report.disbursedAmount)],
      [''],
      ['Approval Rate', this.calculateApprovalRate().toFixed(2) + '%', '-'],
      ['Rejection Rate', this.calculateRejectionRate().toFixed(2) + '%', '-']
    ];

    this.downloadCSV(data, 'loan-summary-report.csv');
  }

  exportEmiReport(): void {
    const report = this.emiReport();
    if (!report) return;

    const data = [
      ['EMI Collection Report'],
      ['Period', `${report.startDate} to ${report.endDate}`],
      [''],
      ['Metric', 'Count', 'Amount'],
      ['Total EMIs Due', report.totalEmisDue, this.formatCurrency(report.totalAmountDue)],
      ['EMIs Collected', report.totalEmisCollected, this.formatCurrency(report.totalAmountCollected)],
      ['EMIs Overdue', report.totalEmisOverdue, this.formatCurrency(report.totalAmountOverdue)],
      [''],
      ['Collection Rate', report.collectionRate.toFixed(2) + '%', '-']
    ];

    this.downloadCSV(data, 'emi-collection-report.csv');
  }

  private formatCurrency(amount: number): string {
    return '₹' + amount.toLocaleString('en-IN');
  }

  private downloadCSV(data: any[][], filename: string): void {
    const csvContent = data.map(row => row.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    this.snackBar.open('Report exported successfully', 'Close', { duration: 3000 });
  }
}
