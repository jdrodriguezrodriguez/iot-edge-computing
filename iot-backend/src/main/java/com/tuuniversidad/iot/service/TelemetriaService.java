package com.tuuniversidad.iot.service;

import com.tuuniversidad.iot.model.Lectura;
import com.tuuniversidad.iot.repository.TelemetriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetriaService {

    private final TelemetriaRepository repository;

    public Lectura guardarLectura(Double temperatura) {
        Lectura lectura = new Lectura();
        lectura.setTemperatura(temperatura);
        lectura.setFecha(LocalDateTime.now());
        Lectura guardada = repository.save(lectura);
        log.info("Lectura guardada: {} {} (id={})",
                guardada.getTemperatura(), guardada.getUnidad(), guardada.getId());
        return guardada;
    }

    public List<Lectura> obtenerTodas() {
        return repository.findAll();
    }

    public List<Lectura> obtenerUltimasDiez() {
        return repository.findTop10ByOrderByFechaDesc();
    }

    public List<Lectura> obtenerPorRangoFecha(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByFechaBetween(inicio, fin);
    }

    public double calcularPromedio() {
        return repository.findAll().stream()
                .filter(l -> l.getTemperatura() != null)
                .mapToDouble(Lectura::getTemperatura)
                .average()
                .orElse(0.0);
    }

    public void eliminarTodas() {
        repository.deleteAll();
        log.warn("Se eliminó todo el historial de telemetría");
    }
}
