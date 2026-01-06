import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { UserService } from '@core/services/user.service';
import { User, UpdateUserRequest } from '@core/models/user.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    PageHeaderComponent
  ],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  profileForm: FormGroup;
  currentUser = signal<User | null>(null);
  isLoading = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  isEditing = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar
  ) {
    this.profileForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  createForm(): FormGroup {
    return this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/), Validators.maxLength(15)]]
    });
  }

  loadUserProfile(): void {
    this.isLoading.set(true);
    this.userService.getCurrentUser().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.currentUser.set(response.data);
          this.populateForm(response.data);
        }
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading user profile:', error);
        this.snackBar.open('Failed to load profile. Please try again.', 'Close', { duration: 3000 });
        this.isLoading.set(false);
      }
    });
  }

  populateForm(user: User): void {
    this.profileForm.patchValue({
      fullName: user.fullName,
      email: user.email,
      phoneNumber: user.phoneNumber
    });
    this.profileForm.disable();
  }

  toggleEdit(): void {
    const newEditingState = !this.isEditing();
    this.isEditing.set(newEditingState);

    if (newEditingState) {
      this.profileForm.enable();
    } else {
      // Cancel editing - restore original values
      if (this.currentUser()) {
        this.populateForm(this.currentUser()!);
      }
      this.profileForm.disable();
    }
  }

  onSubmit(): void {
    if (this.profileForm.valid && !this.isSubmitting()) {
      const formValue = this.profileForm.value;
      const request: UpdateUserRequest = {
        fullName: formValue.fullName,
        email: formValue.email,
        phoneNumber: formValue.phoneNumber
      };

      this.isSubmitting.set(true);
      this.userService.updateCurrentUser(request).subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUser.set(response.data);
            this.snackBar.open('Profile updated successfully!', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.isEditing.set(false);
            this.profileForm.disable();
          }
          this.isSubmitting.set(false);
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          const errorMessage = error?.message || 'Failed to update profile. Please try again.';
          this.snackBar.open(errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
          this.isSubmitting.set(false);
        }
      });
    } else {
      this.profileForm.markAllAsTouched();
      this.snackBar.open('Please fill in all required fields correctly.', 'Close', { duration: 3000 });
    }
  }

  getRoleBadges(): string[] {
    return this.currentUser()?.roles || [];
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
