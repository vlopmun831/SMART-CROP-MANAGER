// src/app/core/services/usuario/usuario.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private http = inject(HttpClient);
  private API_URL = 'http://localhost:8099/usuario';

  // Traer todos (para el Admin)
  getUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(this.API_URL);
  }

  // Crear el nuevo agricultor
  crearUsuario(usuario: any): Observable<any> {
    return this.http.post<any>(this.API_URL, usuario);
  }



modificarUsuario(id: number, usuario: any): Observable<any> {
  return this.http.put<any>(`${this.API_URL}/${id}`, usuario);
}

eliminarUsuario(id: number): Observable<void> {
  return this.http.delete<void>(`${this.API_URL}/${id}`);
}
}