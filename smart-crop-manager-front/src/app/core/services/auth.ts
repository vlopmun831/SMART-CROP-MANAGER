import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

interface AuthResponse {
  token: string;
  nombre: string;
  rol: string;
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private router = inject(Router);
  private http = inject(HttpClient);
  private API_URL = `${environment.apiUrl}/auth`; 

  public isAuthenticated = signal<boolean>(!!localStorage.getItem('token'));
  public userRole = signal<string | null>(localStorage.getItem('userRole'));
  public userName = signal<string | null>(localStorage.getItem('userName'));
  public userId = signal<number | null>(Number(localStorage.getItem('userId')) || null)

  login(credentials: { email: string, password: string }) {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('userRole', response.rol); // Cambia 'rol' por el nombre exacto que venga de tu Java
        localStorage.setItem('userName', response.nombre);
        localStorage.setItem('userId', response.id.toString());

      
        this.isAuthenticated.set(true);
        this.userRole.set(response.rol);
        this.userName.set(response.nombre);
        this.userId.set(response.id);
      })
    );
  }

  logout() {
    localStorage.clear(); // Limpia todo de golpe
    this.isAuthenticated.set(false);
    this.userRole.set(null);
    this.userName.set(null);
    this.userId.set(null);

    this.router.navigate(['/login']);
  }
}