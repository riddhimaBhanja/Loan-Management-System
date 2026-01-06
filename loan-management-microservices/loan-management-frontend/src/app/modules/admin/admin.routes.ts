import { Routes } from '@angular/router';
import { roleGuard } from '@core/guards/role.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: 'users',
    loadComponent: () => import('./user-management/user-list.component').then(m => m.UserListComponent),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'loan-types',
    loadComponent: () => import('./loan-type-management/loan-type-list.component').then(m => m.LoanTypeListComponent),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'reports',
    loadComponent: () => import('./reports-dashboard/reports-dashboard.component').then(m => m.ReportsDashboardComponent),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'LOAN_OFFICER'] }
  }
];
