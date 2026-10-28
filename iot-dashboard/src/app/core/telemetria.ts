import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Lectura } from './lectura.model';

@Injectable({
  providedIn: 'root'
})
export class TelemetriaService {
  private readonly http = inject(HttpClient);

  obtenerUltimas(): Observable<Lectura[]> {
    return this.http.get<Lectura[]>('/api/telemetria/ultimas');
  }

  obtenerPromedio(): Observable<{ promedio: number }> {
    return this.http.get<{ promedio: number }>('/api/telemetria/promedio');
  }
}
