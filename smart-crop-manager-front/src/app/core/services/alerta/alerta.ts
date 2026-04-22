import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlertaService {
  private http = inject(HttpClient);
  // Asegúrate de que este puerto y ruta sean los de tu API de Java
  private API_URL = 'http://localhost:8099/alertas'; 

  /**
   * Obtiene las alertas pendientes de un agricultor específico
   * Corresponde a tu Query en Java: findPendientesByUsuarioId
   */
  getAlertasPendientes(idUsuario: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/pendientes/${idUsuario}`);
  }

  getAlertasGlobales(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}`); 
    // Nota: Si en Postman la ruta para ver todas es /alertas/todas, cámbialo aquí.
  }

  /**
   * Cambia el estado de una alerta (ej. de PENDIENTE a RESUELTA)
   */
 cambiarEstado(idAlerta: number, nuevoEstado: string): Observable<any> {
  // AJUSTADO A TU CONTROLLER: /alertas/{id}/resolver
  return this.http.put(`${this.API_URL}/${idAlerta}/resolver`, {}); 
}
}