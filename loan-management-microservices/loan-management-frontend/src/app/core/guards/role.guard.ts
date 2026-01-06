import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredRoles = route.data['roles'] as string[];

  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  const currentUser = authService.getCurrentUser();

  // If user is not loaded, allow the authGuard to handle it
  if (!currentUser) {
    console.warn('Role Guard: No user found in auth service');
    return true; // Let authGuard handle authentication
  }

  if (authService.hasAnyRole(requiredRoles)) {
    return true;
  }

  console.warn('Role Guard: User does not have required roles', { userRoles: currentUser.roles, requiredRoles });
  // Stay on current page or redirect to dashboard
  router.navigate(['/dashboard']);
  return false;
};
