import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { UserService } from '@core/services/user.service';
import { PageHeaderComponent } from '@shared/components/page-header/page-header.component';
import { User } from '@core/models/user.model';
import { UserRolesDialogComponent } from './user-roles-dialog.component';
import { UserFormDialogComponent } from './user-form-dialog.component';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    PageHeaderComponent
  ],
  template: `
    <app-page-header
      title="User Management"
      subtitle="Manage system users and roles"
      [showBackButton]="true"
      backRoute="/dashboard">
      <button actions mat-raised-button color="primary" (click)="openCreateDialog()">
        <mat-icon>add</mat-icon>
        Create New User
      </button>
    </app-page-header>

    <div class="content-container">
      <div class="table-container">
        <table mat-table [dataSource]="users()" class="users-table">
          <ng-container matColumnDef="id">
            <th mat-header-cell *matHeaderCellDef>ID</th>
            <td mat-cell *matCellDef="let user">{{ user.id }}</td>
          </ng-container>

          <ng-container matColumnDef="username">
            <th mat-header-cell *matHeaderCellDef>Username</th>
            <td mat-cell *matCellDef="let user">{{ user.username }}</td>
          </ng-container>

          <ng-container matColumnDef="fullName">
            <th mat-header-cell *matHeaderCellDef>Full Name</th>
            <td mat-cell *matCellDef="let user">{{ user.fullName }}</td>
          </ng-container>

          <ng-container matColumnDef="email">
            <th mat-header-cell *matHeaderCellDef>Email</th>
            <td mat-cell *matCellDef="let user">{{ user.email }}</td>
          </ng-container>

          <ng-container matColumnDef="roles">
            <th mat-header-cell *matHeaderCellDef>Roles</th>
            <td mat-cell *matCellDef="let user">
              <mat-chip *ngFor="let role of user.roles" class="role-chip">
                {{ role }}
              </mat-chip>
            </td>
          </ng-container>

          <ng-container matColumnDef="isActive">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let user">
              <mat-chip [class.active-chip]="user.isActive" [class.inactive-chip]="!user.isActive">
                {{ user.isActive ? 'Active' : 'Inactive' }}
              </mat-chip>
            </td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef>Actions</th>
            <td mat-cell *matCellDef="let user">
              <div class="action-buttons">
                <button mat-icon-button
                        color="primary"
                        matTooltip="Assign Roles"
                        (click)="openRolesDialog(user)">
                  <mat-icon>admin_panel_settings</mat-icon>
                </button>

                <button mat-icon-button
                        [color]="user.isActive ? 'warn' : 'accent'"
                        [matTooltip]="user.isActive ? 'Deactivate User' : 'Activate User'"
                        (click)="toggleUserStatus(user)">
                  <mat-icon>{{ user.isActive ? 'block' : 'check_circle' }}</mat-icon>
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

        <div *ngIf="!isLoading() && users().length === 0" class="no-data">
          <mat-icon>inbox</mat-icon>
          <p>No users found</p>
        </div>
      </div>

      <mat-paginator
        *ngIf="users().length > 0"
        [length]="totalElements()"
        [pageSize]="pageSize"
        [pageIndex]="pageIndex"
        [pageSizeOptions]="[10, 25, 50]"
        (page)="onPageChange($event)">
      </mat-paginator>
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
      background-image: url('https://images.unsplash.com/photo-1550565118-3a14e8d0386f?w=1920&q=95');
      background-size: cover;
      background-position: center;
      background-attachment: fixed;
      opacity: 0.35;
      z-index: -2;
      pointer-events: none;
      animation: backgroundPulse 22s ease-in-out infinite;
    }

    @keyframes backgroundPulse {
      0%, 100% { transform: scale(1); opacity: 0.30; }
      50% { transform: scale(1.06); opacity: 0.40; }
    }

    .content-container::after {
      content: '';
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(79, 172, 254, 0.04) 100%);
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

    .users-table {
      width: 100%;
    }

    .role-chip {
      margin-right: 4px;
      background-color: #3F51B5;
      color: white;
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
export class UserListComponent implements OnInit {
  users = signal<User[]>([]);
  totalElements = signal<number>(0);
  isLoading = signal<boolean>(false);

  displayedColumns: string[] = ['id', 'username', 'fullName', 'email', 'roles', 'isActive', 'actions'];
  pageSize = 10;
  pageIndex = 0;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.userService.getAllUsers(this.pageIndex, this.pageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.users.set(response.data.content);
          this.totalElements.set(response.data.totalElements);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsers();
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(UserFormDialogComponent, {
      width: '700px',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  openRolesDialog(user: User): void {
    const dialogRef = this.dialog.open(UserRolesDialogComponent, {
      width: '600px',
      disableClose: true,
      data: { user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  toggleUserStatus(user: User): void {
    const action = user.isActive ? 'deactivate' : 'activate';
    const actionPast = user.isActive ? 'deactivated' : 'activated';

    if (confirm(`Are you sure you want to ${action} user "${user.fullName}"?\nThis action can be reversed later.`)) {
      const request = user.isActive
        ? this.userService.deactivateUser(user.id)
        : this.userService.activateUser(user.id);

      request.subscribe({
        next: () => {
          this.snackBar.open(`User ${actionPast} successfully`, 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadUsers();
        },
        error: (error) => {
          console.error(`Error ${action}ing user:`, error);
          this.snackBar.open(
            error.error?.message || `Failed to ${action} user`,
            'Close',
            {
              duration: 5000,
              panelClass: ['error-snackbar']
            }
          );
        }
      });
    }
  }
}
