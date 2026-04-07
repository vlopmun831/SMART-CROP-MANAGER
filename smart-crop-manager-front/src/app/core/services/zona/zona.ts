import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Definimos qué trae una Zona (según tus INSERTS de SQL)
export interface Zona {
  id?: number;
  var_cultivo: string;
  ubicacion: string;
  hum_suelo_min_config: number;
  hum_suelo_max_config: number;
  temp_max_config: number;
}

@Injectable({
  providedIn: 'root'
})
export class ZonaService {
  private http = inject(HttpClient);
  private API_URL = 'http://localhost:8099/zonas'; 

  getZonas(): Observable<any[]> {
    return this.http.get<any[]>(this.API_URL);
  }

  // Para que el agricultor solo vea lo suyo
  getZonasPorUsuario(idUsuario: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/usuario/${idUsuario}`);
  }

  //  Para que el Admin pueda crear zonas
  crearZona(nuevaZona: any): Observable<any> {
    return this.http.post<any>(this.API_URL, nuevaZona);
  }
}