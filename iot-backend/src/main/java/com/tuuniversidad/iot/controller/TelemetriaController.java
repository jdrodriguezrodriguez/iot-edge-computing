package com.tuuniversidad.iot.controller;

import com.tuuniversidad.iot.model.Lectura;
import com.tuuniversidad.iot.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/telemetria")
@RequiredArgsConstructor
@Tag(name = "Telemetría", description = "Lecturas de temperatura recibidas por MQTT")
public class TelemetriaController {

    private final TelemetriaService service;

    @GetMapping
    @Operation(summary = "Obtener todas las lecturas")
    public List<Lectura> obtenerTodas() {
        return service.obtenerTodas();
    }

    @GetMapping("/ultimas")
    @Operation(summary = "Obtener las últimas 10 lecturas")
    public List<Lectura> obtenerUltimas() {
        return service.obtenerUltimasDiez();
    }

    @GetMapping("/promedio")
    @Operation(summary = "Calcular el promedio de temperatura")
    public Map<String, Double> obtenerPromedio() {
        return Map.of("promedio", service.calcularPromedio());
    }

    @GetMapping("/rango")
    @Operation(summary = "Obtener lecturas entre dos fechas (ISO-8601, ej. 2025-01-31T10:00:00)")
    public List<Lectura> obtenerPorRango(
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime fin) {
        return service.obtenerPorRangoFecha(inicio, fin);
    }

    @PostMapping("/manual")
    @Operation(summary = "Registrar una lectura manualmente (pruebas sin MQTT)")
    public ResponseEntity<Lectura> registrarManual(@RequestBody Map<String, Double> body) {
        Double temperatura = body.get("temperatura");
        if (temperatura == null) {
            return ResponseEntity.badRequest().build();
        }
        Lectura guardada = service.guardarLectura(temperatura);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @DeleteMapping
    @Operation(summary = "Borrar todo el historial de lecturas")
    public Map<String, String> eliminarTodas() {
        service.eliminarTodas();
        return Map.of("mensaje", "Historial de telemetría eliminado correctamente");
    }
}
