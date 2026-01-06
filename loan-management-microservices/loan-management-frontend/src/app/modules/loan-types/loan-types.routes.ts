import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';

export const LOAN_TYPES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./loan-types-view.component').then(m => m.LoanTypesViewComponent),
    canActivate: [authGuard]
  }
];
