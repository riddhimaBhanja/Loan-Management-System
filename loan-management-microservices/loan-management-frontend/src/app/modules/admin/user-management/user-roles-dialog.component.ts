import { Component, Inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '@core/services/user.service';
import { User } from '@core/models/user.model';

@Component({
  selector: 'app-user-roles-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>Update User Roles</h2>
    <mat-dialog-content>
      <div class="user-info">
        <p><strong>User:</strong> {{ data.user.fullName }} ({{ data.user.username }})</p>
        <p><strong>Email:</strong> {{ data.user.email }}</p>
      </div>

      <form [formGroup]="rolesForm" class="roles-form">
        <div class="roles-container">
          <h3>Select Roles</h3>
          <div *ngFor="let role of availableRoles()" class="role-checkbox">
            <mat-checkbox
              [checked]="isRoleSelected(role)"
              (change)="toggleRole(role, $event.checked)">
              {{ role }}
            </mat-checkbox>
          </div>
        </div>
      </form>

      <div *ngIf="isLoading()" class="loading-overlay">
        <mat-spinner diameter="40"></mat-spinner>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isLoading()">Cancel</button>
      <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="isLoading() || selectedRoles().length === 0">
        Update Roles
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content {
      min-width: 500px;
      min-height: 300px;
      position: relative;
    }

    .user-info {
      padding: 16px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.06) 100%);
      border-radius: 12px;
      margin-bottom: 24px;

      p {
        margin: 8px 0;
        color: rgba(0, 0, 0, 0.87);

        strong {
          color: rgba(0, 0, 0, 0.6);
          font-weight: 600;
        }
      }
    }

    .roles-form {
      padding: 8px 0;
    }

    .roles-container {
      h3 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
        color: rgba(0, 0, 0, 0.87);
      }
    }

    .role-checkbox {
      padding: 12px 8px;
      margin: 4px 0;
      border-radius: 8px;
      transition: background-color 0.3s ease;

      &:hover {
        background: rgba(102, 126, 234, 0.04);
      }

      mat-checkbox {
        width: 100%;

        ::ng-deep .mdc-checkbox__background {
          border-color: #667eea !important;
        }

        ::ng-deep .mdc-checkbox--selected .mdc-checkbox__background {
          background-color: #667eea !important;
          border-color: #667eea !important;
        }
      }
    }

    .loading-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.9);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 10;
      border-radius: 4px;
    }

    mat-dialog-actions {
      padding: 16px 24px;
      margin: 0;

      button {
        margin-left: 8px;
      }
    }
  `]
})
export class UserRolesDialogComponent implements OnInit {
  rolesForm: FormGroup;
  availableRoles = signal<string[]>([]);
  selectedRoles = signal<string[]>([]);
  isLoading = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<UserRolesDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { user: User }
  ) {
    this.rolesForm = this.fb.group({});
  }

  ngOnInit(): void {
    // Initialize selected roles from user data
    this.selectedRoles.set([...this.data.user.roles]);

    // Load available roles
    this.loadAvailableRoles();
  }

  loadAvailableRoles(): void {
    this.isLoading.set(true);
    this.userService.getAllRoles().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.availableRoles.set(response.data);
        }
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading roles:', error);
        this.snackBar.open('Failed to load available roles', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.isLoading.set(false);
      }
    });
  }

  isRoleSelected(role: string): boolean {
    return this.selectedRoles().includes(role);
  }

  toggleRole(role: string, checked: boolean): void {
    const currentRoles = [...this.selectedRoles()];

    if (checked) {
      if (!currentRoles.includes(role)) {
        currentRoles.push(role);
      }
    } else {
      const index = currentRoles.indexOf(role);
      if (index > -1) {
        currentRoles.splice(index, 1);
      }
    }

    this.selectedRoles.set(currentRoles);
  }

  onSubmit(): void {
    if (this.selectedRoles().length === 0) {
      this.snackBar.open('At least one role must be selected', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.isLoading.set(true);

    this.userService.updateUserRoles(this.data.user.id, { roles: this.selectedRoles() }).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.snackBar.open('User roles updated successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error updating roles:', error);
        this.isLoading.set(false);
        this.snackBar.open(
          error.error?.message || 'Failed to update user roles',
          'Close',
          {
            duration: 5000,
            panelClass: ['error-snackbar']
          }
        );
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
