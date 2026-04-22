import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ZonaService {
  private http = inject(HttpClient);
  private API_URL = 'http://localhost:8099/zonas'; // Ajusta si tu puerto es distinto

  // --- MÉTODOS DE CONSULTA ---

  getZonas(): Observable<any[]> {
    return this.http.get<any[]>(this.API_URL);
  }

  getZonasPorUsuario(idUsuario: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/usuario/${idUsuario}`);
  }

  getZonaDetalle(idZona: number): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/${idZona}`);
  }

  // --- MÉTODOS DE GESTIÓN (ADMIN) ---

  crearZona(zona: any): Observable<any> {
    return this.http.post(this.API_URL, zona);
  }

  modificarZona(id: number, zona: any): Observable<any> {
    return this.http.put(`${this.API_URL}/${id}`, zona);
  }

  eliminarZona(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`);
  }

  // --- MÉTODOS DE OPERACIÓN (RIEGO) ---
  // Según tu Postman: POST para iniciar, PUT para finalizar

  iniciarRiego(idZona: number): Observable<any> {
    // Si tu backend espera un objeto vacío o datos, lo enviamos aquí
    return this.http.post(`${this.API_URL}/${idZona}/riego/iniciar`, {});
  }

  finalizarRiego(idZona: number): Observable<any> {
    return this.http.put(`${this.API_URL}/${idZona}/riego/finalizar`, {});
  }

  // --- MÉTODOS DE DATOS HISTÓRICOS ---

  getHistorialDatos(idZona: number): Observable<any[]> {
    // Este servirá para las gráficas que haremos después
    return this.http.get<any[]>(`http://localhost:8099/registros/zona/${idZona}`);
  }
}