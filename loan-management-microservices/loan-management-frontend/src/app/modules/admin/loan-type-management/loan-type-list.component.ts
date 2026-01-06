import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { LoanTypeService } from '@core/services/loan-type.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { InrCurrencyPipe } from '@shared/pipes/inr-currency.pipe';
import { LoanType } from '@core/models/loan-type.model';
import { LoanTypeFormDialogComponent } from './loan-type-form-dialog.component';

@Component({
  selector: 'app-loan-type-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDialogModule,
    PageHeaderComponent,
    InrCurrencyPipe
  ],
  template: `
    <app-page-header
      title="Loan Type Management"
      subtitle="Manage loan types and configurations"
      [showBackButton]="true"
      backRoute="/dashboard">
      <button actions mat-raised-button color="primary" (click)="openCreateDialog()">
        <mat-icon>add</mat-icon>
        Create New Loan Type
      </button>
    </app-page-header>

    <div class="content-container">
      <div class="table-container">
        <table mat-table [dataSource]="loanTypes()" class="loan-types-table">
          <ng-container matColumnDef="id">
            <th mat-header-cell *matHeaderCellDef>ID</th>
            <td mat-cell *matCellDef="let type">{{ type.id }}</td>
          </ng-container>

          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Name</th>
            <td mat-cell *matCellDef="let type">{{ type.name }}</td>
          </ng-container>

          <ng-container matColumnDef="description">
            <th mat-header-cell *matHeaderCellDef>Description</th>
            <td mat-cell *matCellDef="let type">{{ type.description }}</td>
          </ng-container>

          <ng-container matColumnDef="amountRange">
            <th mat-header-cell *matHeaderCellDef>Amount Range</th>
            <td mat-cell *matCellDef="let type">
              {{ type.minAmount | inrCurrency }} - {{ type.maxAmount | inrCurrency }}
            </td>
          </ng-container>

          <ng-container matColumnDef="tenureRange">
            <th mat-header-cell *matHeaderCellDef>Tenure (Months)</th>
            <td mat-cell *matCellDef="let type">
              {{ type.minTenureMonths }} - {{ type.maxTenureMonths }}
            </td>
          </ng-container>

          <ng-container matColumnDef="interestRate">
            <th mat-header-cell *matHeaderCellDef>Interest Rate</th>
            <td mat-cell *matCellDef="let type">{{ type.interestRate }}% p.a.</td>
          </ng-container>

          <ng-container matColumnDef="isActive">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let type">
              <mat-chip [class.active-chip]="type.isActive" [class.inactive-chip]="!type.isActive">
                {{ type.isActive ? 'Active' : 'Inactive' }}
              </mat-chip>
            </td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef>Actions</th>
            <td mat-cell *matCellDef="let type">
              <div class="action-buttons">
                <button mat-icon-button
                        color="primary"
                        matTooltip="Edit Loan Type"
                        (click)="openEditDialog(type)">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button
                        [color]="type.isActive ? 'warn' : 'accent'"
                        [matTooltip]="type.isActive ? 'Deactivate Loan Type' : 'Activate Loan Type'"
                        (click)="toggleLoanTypeStatus(type)">
                  <mat-icon>{{ type.isActive ? 'block' : 'check_circle' }}</mat-icon>
                </button>
              </div>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
        </table>

        <div *ngIf="isLoading()" class="loading-overlay">
          <mat-spinner></mat-spinner>
        </div>

        <div *ngIf="!isLoading() && loanTypes().length === 0" class="no-data">
          <mat-icon>inbox</mat-icon>
          <p>No loan types found</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .content-container {
      padding: 32px 24px;
      position: relative;
      z-index: 1;
      min-height: 100vh;
    }

    .content-container::before {
      content: '';
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image: url('https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=1920&q=95');
      background-size: cover;
      background-position: center;
      background-attachment: fixed;
      opacity: 0.35;
      z-index: -2;
      pointer-events: none;
      animation: backgroundFloat 20s ease-in-out infinite;
    }

    @keyframes backgroundFloat {
      0%, 100% { transform: scale(1) translateY(0); opacity: 0.30; }
      50% { transform: scale(1.05) translateY(-10px); opacity: 0.40; }
    }

    .content-container::after {
      content: '';
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(240, 147, 251, 0.04) 100%);
      z-index: -1;
      pointer-events: none;
    }

    .table-container {
      position: relative;
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }

    .loan-types-table {
      width: 100%;
    }

    .active-chip {
      background-color: #4CAF50;
      color: white;
    }

    .inactive-chip {
      background-color: #9E9E9E;
      color: white;
    }

    .loading-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.8);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 10;
    }

    .no-data {
      padding: 48px;
      text-align: center;
      color: rgba(0, 0, 0, 0.54);

      mat-icon {
        font-size: 64px;
        width: 64px;
        height: 64px;
        margin-bottom: 16px;
        opacity: 0.5;
      }

      p {
        margin: 16px 0;
        font-size: 16px;
      }
    }

    .action-buttons {
      display: flex;
      gap: 8px;
      align-items: center;

      button {
        transition: transform 0.2s ease;

        &:hover {
          transform: scale(1.1);
        }
      }
    }
  `]
})
export class LoanTypeListComponent implements OnInit {
  loanTypes = signal<LoanType[]>([]);
  isLoading = signal<boolean>(false);

  displayedColumns: string[] = ['id', 'name', 'description', 'amountRange', 'tenureRange', 'interestRate', 'isActive', 'actions'];

  constructor(
    private loanTypeService: LoanTypeService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadLoanTypes();
  }

  loadLoanTypes(): void {
    this.isLoading.set(true);
    this.loanTypeService.getAllLoanTypes().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.loanTypes.set(response.data);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(LoanTypeFormDialogComponent, {
      width: '700px',
      disableClose: true,
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadLoanTypes();
      }
    });
  }

  openEditDialog(loanType: LoanType): void {
    const dialogRef = this.dialog.open(LoanTypeFormDialogComponent, {
      width: '700px',
      disableClose: true,
      data: { loanType }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadLoanTypes();
      }
    });
  }

  toggleLoanTypeStatus(loanType: LoanType): void {
    const action = loanType.isActive ? 'deactivate' : 'activate';
    const actionPast = loanType.isActive ? 'deactivated' : 'activated';

    if (confirm(`Are you sure you want to ${action} loan type "${loanType.name}"?\nThis action can be reversed later.`)) {
      this.isLoading.set(true);

      if (loanType.isActive) {
        // Deactivate using DELETE endpoint
        this.loanTypeService.deleteLoanType(loanType.id).subscribe({
          next: () => {
            this.handleToggleSuccess(actionPast);
          },
          error: (error: any) => {
            this.handleToggleError(error, action);
          }
        });
      } else {
        // Activate using UPDATE endpoint with isActive: true
        this.loanTypeService.updateLoanType(loanType.id, {
          ...loanType,
          isActive: true
        }).subscribe({
          next: () => {
            this.handleToggleSuccess(actionPast);
          },
          error: (error: any) => {
            this.handleToggleError(error, action);
          }
        });
      }
    }
  }

  private handleToggleSuccess(actionPast: string): void {
    this.snackBar.open(`Loan type ${actionPast} successfully`, 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
    this.loadLoanTypes();
  }

  private handleToggleError(error: any, action: string): void {
    console.error(`Error ${action}ing loan type:`, error);
    this.isLoading.set(false);

    // Extract error message from various possible structures
    let errorMessage = `Failed to ${action} loan type`;
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
  }
}
