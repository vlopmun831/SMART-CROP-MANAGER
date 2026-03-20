import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'dashboard', component: Dashboard}
];
