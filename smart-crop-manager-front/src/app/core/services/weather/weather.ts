import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, switchMap, of, catchError, forkJoin } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WeatherService {
  private http = inject(HttpClient);
  
  private readonly DEFAULT_LAT = 40.4168;
  private readonly DEFAULT_LON = -3.7038;
  private readonly DEFAULT_CITY = 'Madrid';

  getWeather(): Observable<any> {
    return new Observable<GeolocationPosition>((observer) => {
      if ('geolocation' in navigator) {
        navigator.geolocation.getCurrentPosition(
          (pos) => { observer.next(pos); observer.complete(); },
          (err) => { observer.error(err); },
          { timeout: 10000 }
        );
      } else {
        observer.error('Geolocation not supported');
      }
    }).pipe(
      map(pos => ({ lat: pos.coords.latitude, lon: pos.coords.longitude })),
      catchError(() => of({ lat: this.DEFAULT_LAT, lon: this.DEFAULT_LON })),
      switchMap(coords => {
        // Combinamos la llamada del tiempo con la de obtener el nombre de la ciudad
        return forkJoin({
          weather: this.fetchWeatherData(coords.lat, coords.lon),
          location: this.fetchCityName(coords.lat, coords.lon)
        }).pipe(
          map(res => ({
            ...res.weather,
            city: res.location
          }))
        );
      })
    );
  }

  private fetchWeatherData(lat: number, lon: number): Observable<any> {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto`;
    return this.http.get<any>(url).pipe(
      map(res => ({
        temp: res.current.temperature_2m,
        humidity: res.current.relative_humidity_2m,
        wind: res.current.wind_speed_10m,
        code: res.current.weather_code,
        description: this.getWeatherDescription(res.current.weather_code),
        icon: this.getWeatherIcon(res.current.weather_code)
      }))
    );
  }

  private fetchCityName(lat: number, lon: number): Observable<string> {
    // Usamos Nominatim (OpenStreetMap) para reverse geocoding
    const url = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`;
    return this.http.get<any>(url).pipe(
      map(res => {
        return res.address.city || res.address.town || res.address.village || res.address.suburb || this.DEFAULT_CITY;
      }),
      catchError(() => of(this.DEFAULT_CITY))
    );
  }

  private getWeatherDescription(code: number): string {
    if (code === 0) return 'Cielo Despejado';
    if (code <= 3) return 'Parcialmente Nublado';
    if (code <= 48) return 'Niebla';
    if (code <= 67) return 'Lluvia Ligera';
    if (code <= 82) return 'Lluvia Intensa';
    if (code <= 99) return 'Tormenta';
    return 'Nublado';
  }

  private getWeatherIcon(code: number): string {
    if (code === 0) return '☀️';
    if (code <= 3) return '🌤️';
    if (code <= 48) return '🌫️';
    if (code <= 67) return '🌦️';
    if (code <= 82) return '🌧️';
    if (code <= 99) return '⛈️';
    return '☁️';
  }
}
