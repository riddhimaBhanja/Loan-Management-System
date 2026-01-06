import { CanDeactivateFn } from '@angular/router';
import { Observable } from 'rxjs';

/**
 * Interface for components that need to confirm navigation away with unsaved changes
 */
export interface CanComponentDeactivate {
  canDeactivate: () => boolean | Observable<boolean>;
  hasUnsavedChanges?: () => boolean;
}

/**
 * Guard to prevent navigation away from a component with unsaved changes
 *
 * Usage:
 * 1. Implement CanComponentDeactivate interface in your component
 * 2. Add hasUnsavedChanges() method to check form dirty state
 * 3. Add canDeactivate() method to show confirmation dialog
 * 4. Apply guard in route configuration:
 *    { path: 'apply', component: LoanApplyComponent, canDeactivate: [canDeactivateGuard] }
 */
export const canDeactivateGuard: CanDeactivateFn<CanComponentDeactivate> = (
  component: CanComponentDeactivate
): boolean | Observable<boolean> => {
  // Check if component has unsaved changes
  if (component.hasUnsavedChanges && component.hasUnsavedChanges()) {
    // Show confirmation dialog
    const confirmMessage =
      'You have unsaved changes. Do you really want to leave this page? All unsaved data will be lost.';

    return window.confirm(confirmMessage);
  }

  // Allow navigation if no unsaved changes
  return true;
};

/**
 * Alternative: Custom dialog-based confirmation
 *
 * For Material Dialog usage:
 *
 * export const canDeactivateGuard: CanDeactivateFn<CanComponentDeactivate> = (
 *   component: CanComponentDeactivate,
 *   currentRoute: ActivatedRouteSnapshot,
 *   currentState: RouterStateSnapshot,
 *   nextState?: RouterStateSnapshot
 * ): Observable<boolean> | boolean => {
 *
 *   if (component.hasUnsavedChanges && component.hasUnsavedChanges()) {
 *     const dialogRef = inject(MatDialog).open(ConfirmDialogComponent, {
 *       data: {
 *         title: 'Unsaved Changes',
 *         message: 'You have unsaved changes. Do you want to leave without saving?',
 *         confirmText: 'Leave',
 *         cancelText: 'Stay'
 *       }
 *     });
 *
 *     return dialogRef.afterClosed().pipe(
 *       map(result => result === true)
 *     );
 *   }
 *
 *   return true;
 * };
 */
