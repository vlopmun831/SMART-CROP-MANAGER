import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    // Cargamos el componente de login que acabas de crear
    loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    // Este lo crearemos después para ver tus tareas
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent)
  },
  {
    // Si la URL está vacía (http://localhost:4200), redirigimos al login automáticamente
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    // Si el usuario escribe cualquier otra cosa que no existe, lo mandamos al login
    path: '**',
    redirectTo: 'login'
  }
];