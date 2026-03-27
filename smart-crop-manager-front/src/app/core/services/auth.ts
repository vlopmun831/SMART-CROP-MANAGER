import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

interface AuthResponse {
  token: string;
  nombre: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private API_URL = 'http://localhost:8099/auth'; 

  public isAuthenticated = signal<boolean>(!!localStorage.getItem('token'));
  public userRole = signal<string | null>(localStorage.getItem('userRole'));
  public userName = signal<string | null>(localStorage.getItem('email'));

  login(credentials: { email: string, password: string }) {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('userRole', response.rol); // Cambia 'rol' por el nombre exacto que venga de tu Java
        localStorage.setItem('userName', response.nombre);

        this.isAuthenticated.set(true);
        this.userRole.set(response.rol);
        this.userName.set(response.nombre);
      })
    );
  }

  logout() {
    localStorage.clear(); // Limpia todo de golpe
    this.isAuthenticated.set(false);
    this.userRole.set(null);
    this.userName.set(null);
  }
}