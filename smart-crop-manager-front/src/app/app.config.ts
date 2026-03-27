import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http'; // Añade esto
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth-interceptor'; // Lo crearemos ahora

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])) // Registramos el interceptor aquí
  ]
};