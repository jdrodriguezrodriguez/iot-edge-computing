import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { EMPTY, Subject, interval } from 'rxjs';
import { catchError, startWith, switchMap, takeUntil } from 'rxjs/operators';

import { Lectura } from '../core/lectura.model';
import { TelemetriaService } from '../core/telemetria';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit, OnDestroy {
  private readonly telemetriaService = inject(TelemetriaService);
  private readonly destruir$ = new Subject<void>();

  readonly lecturas = signal<Lectura[]>([]);
  readonly cargando = signal(true);
  readonly errorMensaje = signal<string | null>(null);

  chartData: any[] = [];

  get ultimaLectura(): Lectura | undefined {
    const datos = this.lecturas();
    return datos.length ? datos[datos.length - 1] : undefined;
  }

  get promedio(): number | null {
    const datos = this.lecturas();

    if (!datos.length) {
      return null;
    }

    const suma = datos.reduce(
      (total, lectura) => total + lectura.temperatura,
      0
    );

    return suma / datos.length;
  }

  get gaugeData(): { name: string; value: number }[] {
    const lectura = this.ultimaLectura;

    return lectura
      ? [{ name: 'Temperatura', value: lectura.temperatura }]
      : [];
  }

  ngOnInit(): void {
    interval(5000)
      .pipe(
        startWith(0),
        switchMap(() =>
          this.telemetriaService.obtenerUltimas().pipe(
            catchError(() => {
              this.errorMensaje.set(
                'No se pudo consultar el backend. Verifica que Spring Boot esté ejecutándose.'
              );
              this.cargando.set(false);
              return EMPTY;
            })
          )
        ),
        takeUntil(this.destruir$)
      )
      .subscribe((respuesta) => {
        const ordenadas = [...respuesta].sort(
          (a, b) =>
            new Date(a.fecha).getTime() - new Date(b.fecha).getTime()
        );

        this.lecturas.set(ordenadas);

        this.chartData = [
          {
            name: 'Temperatura',
            series: ordenadas.map((lectura) => ({
              name: new Date(lectura.fecha),
              value: lectura.temperatura
            }))
          }
        ];

        this.errorMensaje.set(null);
        this.cargando.set(false);
      });
  }

  ngOnDestroy(): void {
    this.destruir$.next();
    this.destruir$.complete();
  }
}
