import {HttpInterceptorFn} from '@angular/common/http';

// Interceptor adds token to Header for every service in frontend.
// Without interceptor every service would need to add the token themselves
// -> registered in app.config.ts

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // Only login and registration happen before a token exists
  const isLogin = req.method === 'POST' && req.url.endsWith('/api/auth/login');
  const isRegister = req.method === 'POST' && req.url.endsWith('/api/users');

  if (isLogin || isRegister) {
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
