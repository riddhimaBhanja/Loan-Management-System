import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  // Debug logging
  if (req.url.includes('/api/')) {
    console.log('Auth Interceptor - URL:', req.url);
    console.log('Auth Interceptor - Has Token:', !!token);
    if (token) {
      console.log('Auth Interceptor - Token Preview:', token.substring(0, 20) + '...');
    }
  }

  if (token && !req.url.includes('/auth/')) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }

  return next(req);
};
