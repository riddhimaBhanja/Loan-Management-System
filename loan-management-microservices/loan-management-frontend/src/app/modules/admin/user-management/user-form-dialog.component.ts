import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { UserService } from '@core/services/user.service';
import { CreateUserRequest } from '@core/models/user.model';

@Component({
  selector: 'app-user-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSelectModule
  ],
  template: `
    <h2 mat-dialog-title>Create New User</h2>

    <mat-dialog-content>
      <form [formGroup]="userForm" class="user-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Username</mat-label>
          <input matInput formControlName="username" placeholder="e.g., john.doe" maxlength="50" />
          @if (userForm.get('username')?.invalid && userForm.get('username')?.touched) {
            <mat-error>
              @if (userForm.get('username')?.hasError('required')) {
                Username is required
              }
              @if (userForm.get('username')?.hasError('pattern')) {
                Username can only contain letters, numbers, dots and underscores
              }
            </mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Full Name</mat-label>
          <input matInput formControlName="fullName" placeholder="e.g., John Doe" maxlength="100" />
          @if (userForm.get('fullName')?.invalid && userForm.get('fullName')?.touched) {
            <mat-error>Full name is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email" placeholder="e.g., john.doe@example.com" maxlength="100" />
          @if (userForm.get('email')?.invalid && userForm.get('email')?.touched) {
            <mat-error>
              @if (userForm.get('email')?.hasError('required')) {
                Email is required
              }
              @if (userForm.get('email')?.hasError('email')) {
                Please enter a valid email address
              }
            </mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Phone Number</mat-label>
          <input matInput formControlName="phoneNumber" placeholder="e.g., +91-9876543210" maxlength="20" />
          @if (userForm.get('phoneNumber')?.invalid && userForm.get('phoneNumber')?.touched) {
            <mat-error>Phone number is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Password</mat-label>
          <input matInput type="password" formControlName="password" placeholder="Min. 8 characters" maxlength="100" />
          @if (userForm.get('password')?.invalid && userForm.get('password')?.touched) {
            <mat-error>
              @if (userForm.get('password')?.hasError('required')) {
                Password is required
              }
              @if (userForm.get('password')?.hasError('minlength')) {
                Password must be at least 8 characters
              }
            </mat-error>
          }
          <mat-hint>Must be at least 8 characters long</mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Role</mat-label>
          <mat-select formControlName="role">
            <mat-option value="CUSTOMER">Customer</mat-option>
            <mat-option value="LOAN_OFFICER">Loan Officer</mat-option>
            <mat-option value="ADMIN">Admin</mat-option>
          </mat-select>
          @if (userForm.get('role')?.invalid && userForm.get('role')?.touched) {
            <mat-error>Role is required</mat-error>
          }
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isSubmitting()">Cancel</button>
      <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="userForm.invalid || isSubmitting()">
        @if (isSubmitting()) {
          <mat-spinner diameter="20"></mat-spinner>
        } @else {
          <span>Create User</span>
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

    .user-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .full-width {
      width: 100%;
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
    }
  `]
})
export class UserFormDialogComponent {
  userForm: FormGroup;
  isSubmitting = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UserFormDialogComponent>,
    private userService: UserService,
    private snackBar: MatSnackBar
  ) {
    this.userForm = this.createForm();
  }

  createForm(): FormGroup {
    return this.fb.group({
      username: ['', [
        Validators.required,
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9._]+$/)
      ]],
      fullName: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      phoneNumber: ['', [Validators.required, Validators.maxLength(20)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(100)]],
      role: ['CUSTOMER', [Validators.required]]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      this.isSubmitting.set(true);
      this.createUser();
    }
  }

  createUser(): void {
    const formValue = this.userForm.value;
    const request: CreateUserRequest = {
      ...formValue,
      roles: [formValue.role] // Convert single role to array of roles
    };

    this.userService.createUser(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.snackBar.open('User created successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.dialogRef.close(true);
        }
        this.isSubmitting.set(false);
      },
      error: (error) => {
        console.error('Error creating user:', error);

        // Extract error message from various possible structures
        let errorMessage = 'Failed to create user. Please try again.';
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
