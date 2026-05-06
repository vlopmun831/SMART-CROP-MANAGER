import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlertaService {
  private http = inject(HttpClient);
  private API_URL = 'http://localhost:8099/alertas';

  /**
   * Obtiene las alertas pendientes de un usuario específico
   * Endpoint según Postman: GET /alertas/usuario/{id}/pendientes
   */
  getAlertasPendientes(idUsuario: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/usuario/${idUsuario}/pendientes`);
  }

  /**
   * Obtiene TODAS las alertas del sistema (para el panel ADMIN)
   */
  getAlertasGlobales(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}`);
  }

  /**
   * Marca una alerta como resuelta
   * Endpoint según Postman: PUT /alertas/{id}/resolver
   */
  resolverAlerta(idAlerta: number): Observable<any> {
    return this.http.put(`${this.API_URL}/${idAlerta}/resolver`, {});
  }
}