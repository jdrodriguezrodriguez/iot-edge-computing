package com.tuuniversidad.iot.repository;

import com.tuuniversidad.iot.model.Lectura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TelemetriaRepository extends MongoRepository<Lectura, String> {

    List<Lectura> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Lectura> findTop10ByOrderByFechaDesc();
}
