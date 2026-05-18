import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AlertaService {
  private http = inject(HttpClient);
  private API_URL = `${environment.apiUrl}/alertas`;

  getAlertasPendientes(idUsuario: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/usuario/${idUsuario}/pendientes`);
  }

  getAlertasGlobales(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}`);
  }

  /**
   * Cambia el estado de una alerta llamando al endpoint específico del backend
   */
  cambiarEstadoAlerta(idAlerta: number, nuevoEstado: string): Observable<any> {
    // Si es RESUELTA, llama a /alertas/{id}/resolver
    // Si es IGNORADA, llama a /alertas/{id}/ignorar
    const endpoint = nuevoEstado === 'RESUELTA' ? 'resolver' : 'ignorar';
    return this.http.put(`${this.API_URL}/${idAlerta}/${endpoint}`, {});
  }

  resolverAlerta(idAlerta: number): Observable<any> {
    return this.cambiarEstadoAlerta(idAlerta, 'RESUELTA');
  }
}