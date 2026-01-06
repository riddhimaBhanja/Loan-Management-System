import { Routes } from '@angular/router';
import { roleGuard } from '@core/guards/role.guard';

export const EMI_ROUTES: Routes = [
  {
    path: 'loan/:loanId',
    loadComponent: () => import('./emi-schedule/emi-schedule.component').then(m => m.EmiScheduleComponent)
  },
  {
    path: 'my-schedule',
    loadComponent: () => import('./my-emi-schedule/my-emi-schedule.component').then(m => m.MyEmiScheduleComponent),
    canActivate: [roleGuard],
    data: { roles: ['CUSTOMER'] }
  },
  {
    path: 'overdue',
    loadComponent: () => import('./overdue-emis/overdue-emis.component').then(m => m.OverdueEmisComponent),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'LOAN_OFFICER'] }
  }
];
