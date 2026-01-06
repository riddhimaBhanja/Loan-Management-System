import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '@core/services/auth.service';
import { User } from '@core/models/user.model';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles?: string[];
}

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule
  ],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {
  currentUser = signal<User | null>(null);
  sidenavOpened = signal(true);

  navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'dashboard' },
    { label: 'Loan Types', route: '/loan-types', icon: 'category', roles: ['CUSTOMER'] },
    { label: 'Apply for Loan', route: '/loans/apply', icon: 'add_circle', roles: ['CUSTOMER'] },
    { label: 'My Loans', route: '/loans/my-loans', icon: 'account_balance', roles: ['CUSTOMER'] },
    { label: 'My EMI Schedule', route: '/emis/my-schedule', icon: 'schedule', roles: ['CUSTOMER'] },
    { label: 'All Loans', route: '/loans/all', icon: 'list', roles: ['ADMIN', 'LOAN_OFFICER'] },
    { label: 'Assigned Loans', route: '/loans/assigned', icon: 'assignment', roles: ['LOAN_OFFICER'] },
    { label: 'Overdue EMIs', route: '/emis/overdue', icon: 'warning', roles: ['ADMIN', 'LOAN_OFFICER'] },
    { label: 'Reports', route: '/admin/reports', icon: 'assessment', roles: ['ADMIN', 'LOAN_OFFICER'] },
    { label: 'Users', route: '/admin/users', icon: 'people', roles: ['ADMIN'] },
    { label: 'Manage Loan Types', route: '/admin/loan-types', icon: 'settings', roles: ['ADMIN'] },
    { label: 'My Profile', route: '/profile', icon: 'account_circle' }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser.set(user);
    });
  }

  toggleSidenav(): void {
    this.sidenavOpened.set(!this.sidenavOpened());
  }

  shouldShowNavItem(item: NavItem): boolean {
    if (!item.roles || item.roles.length === 0) {
      return true;
    }
    return this.authService.hasAnyRole(item.roles);
  }

  logout(): void {
    this.authService.logout();
  }

  getUserDisplayName(): string {
    const user = this.currentUser();
    return user ? user.fullName : 'User';
  }

  getUserRole(): string {
    const user = this.currentUser();
    if (!user || !user.roles || user.roles.length === 0) {
      return '';
    }
    return user.roles[0].replace('_', ' ');
  }
}
