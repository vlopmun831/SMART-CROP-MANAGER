import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private API_URL = 'http://localhost:8099/auth'; 

  public isAuthenticated = signal<boolean>(!!localStorage.getItem('token'));

  login(credentials: { username: string, password: string }) {
    return this.http.post<{token: string}>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        this.isAuthenticated.set(true);
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    this.isAuthenticated.set(false);
  }
}