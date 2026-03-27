import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Comprobamos el Signal de autenticación
  if (authService.isAuthenticated()) {
    return true; // Tienes token, puedes pasar
  } else {
    // No estás logueado, te mandamos al login
    router.navigate(['/auth/login']); 
    return false;
  }
};