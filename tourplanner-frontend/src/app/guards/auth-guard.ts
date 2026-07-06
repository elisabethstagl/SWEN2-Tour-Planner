import {inject} from '@angular/core';
import {CanActivateFn, Router, UrlTree} from '@angular/router';
import {AuthService} from '../service/auth-service';
import {catchError, map, Observable, of} from 'rxjs';

// Protects private routes.
// Allows access only if the user is logged in and the JWT is still valid.

export const authGuard: CanActivateFn = (): boolean | UrlTree | Observable<boolean | UrlTree> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Redirect unauthenticated users to the home/start page.
  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/']);
  }

  // Validate the stored JWT with the backend
  return authService.validateToken().pipe(
    map(() => true),

    // Token is invalid or expired -> log out and redirect
    catchError(() => {
      authService.logout();
      return of(router.createUrlTree(['/']));
    })
  );
};


// Prevents authenticated users from accessing public pages
// such as the login or registration page.

export const publicOnlyGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Redirect logged-in users to the main application.
  return authService.isLoggedIn()
    ? router.createUrlTree(['/app'])
    : true;
};
