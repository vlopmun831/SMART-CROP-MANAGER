import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Asegúrate de que este puerto coincide con el de tu Eclipse
  private API_URL = 'http://localhost:8099/auth'; 

  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, { email, password });
  }

  saveToken(token: string) {
    localStorage.setItem('token', token);
  }
}