import {HttpInterceptorFn} from '@angular/common/http';

// Interceptor adds token to Header for every service in frontend.
// Without interceptor every service would need to add the token themselves
// -> registered in app.config.ts

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  if (req.url.includes('/api/auth/login') || req.url.includes('/api/users')) {
    return next(req);
  }

  const token = localStorage.getItem('token');

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
