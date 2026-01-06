import { Routes } from '@angular/router';
import { roleGuard } from '@core/guards/role.guard';

export const LOAN_ROUTES: Routes = [
  {
    path: 'apply',
    loadComponent: () => import('./loan-apply/loan-apply.component').then(m => m.LoanApplyComponent),
    canActivate: [roleGuard],
    data: { roles: ['CUSTOMER'] }
  },
  {
    path: 'my-loans',
    loadComponent: () => import('./loan-list/loan-list.component').then(m => m.LoanListComponent),
    canActivate: [roleGuard],
    data: { roles: ['CUSTOMER'] }
  },
  {
    path: 'all',
    loadComponent: () => import('./loan-list/loan-list.component').then(m => m.LoanListComponent),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'LOAN_OFFICER'] }
  },
  {
    path: 'assigned',
    loadComponent: () => import('./loan-list/loan-list.component').then(m => m.LoanListComponent),
    canActivate: [roleGuard],
    data: { roles: ['LOAN_OFFICER'] }
  },
  {
    path: '',
    loadComponent: () => import('./loan-list/loan-list.component').then(m => m.LoanListComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./loan-detail/loan-detail.component').then(m => m.LoanDetailComponent)
  },
  {
    path: ':id/review',
    loadComponent: () => import('./loan-review/loan-review.component').then(m => m.LoanReviewComponent),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'LOAN_OFFICER'] }
  }
];
