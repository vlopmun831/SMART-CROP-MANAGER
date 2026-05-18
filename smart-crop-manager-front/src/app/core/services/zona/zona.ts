import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ZonaService {
  private http = inject(HttpClient);
  private API_URL = `${environment.apiUrl}/zonas`;

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

  // --- RIEGO ---
  // POST /riego/zona/{zonaId}/iniciar  → inicia y devuelve el objeto Riego con su id
  iniciarRiego(idZona: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/riego/zona/${idZona}/iniciar`, {});
  }

  // PUT /riego/{riegoId}/finalizar  → usa el ID del registro de riego, NO el de la zona
  finalizarRiego(idRiego: number): Observable<any> {
    return this.http.put<any>(`${environment.apiUrl}/riego/${idRiego}/finalizar`, {});
  }

  // GET /riego/zona/{idZona}/historial → historial de sesiones de riego
  getHistorialRiego(idZona: number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/riego/zona/${idZona}/historial`);
  }

  // --- HISTÓRICO DE DATOS DE SENSORES ---

  // GET /registros/zona/{zonaId}  → historial completo
  getHistorialDatos(idZona: number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/registros/zona/${idZona}`);
  }

  // GET /registros/zona/{zonaId}/ultimo  → último dato (para el dashboard en tiempo real)
  getUltimoRegistro(idZona: number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/registros/zona/${idZona}/ultimo`);
  }
}