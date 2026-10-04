import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { Auth } from './auth';

const PUBLIC_PATHS = ['/auth/login', '/auth/register', '/auth/google', '/auth/forgot-password', '/auth/reset-password'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);
  const router = inject(Router);
  const token = auth.getToken();

  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  return next(req).pipe(
    catchError((err) => {
      // Token hết hạn / bị hủy -> buộc đăng nhập lại
      const isPublic = PUBLIC_PATHS.some((p) => req.url.includes(p));
      if (err.status === 401 && !isPublic && auth.isLoggedIn()) {
        auth.clearSession();
        router.navigate(['/login'], { queryParams: { expired: '1' } });
      }
      return throwError(() => err);
    })
  );
};
