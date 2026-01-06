import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An error occurred';

      if (error.error instanceof ErrorEvent) {
        errorMessage = `Client Error: ${error.error.message}`;
      } else {
        // Handle status 0 (network error or CORS issue)
        if (error.status === 0) {
          errorMessage = 'Unable to connect to server. Please ensure the backend is running.';
        } else {
          switch (error.status) {
            case 400:
              // Extract validation errors if present
              if (error.error?.error?.message) {
                errorMessage = error.error.error.message;
              } else if (error.error?.message) {
                errorMessage = error.error.message;
              } else if (typeof error.error === 'string') {
                errorMessage = error.error;
              } else {
                errorMessage = 'Bad Request - Please check your input';
              }
              break;
            case 401:
              errorMessage = 'Unauthorized access. Please login again.';
              console.warn('401 Unauthorized:', req.url);
              authService.logout();
              router.navigate(['/auth/login']);
              break;
            case 403:
              errorMessage = 'Access forbidden. You don\'t have permission.';
              break;
            case 404:
              errorMessage = error.error?.message || 'Resource not found';
              break;
            case 409:
              errorMessage = error.error?.message || 'Conflict occurred';
              break;
            case 500:
              errorMessage = 'Internal server error. Please try again later.';
              break;
            default:
              errorMessage = error.error?.message || `Server Error: ${error.status}`;
          }
        }
      }

      console.error('HTTP Error:', errorMessage, error);
      return throwError(() => ({ message: errorMessage, error }));
    })
  );
};
