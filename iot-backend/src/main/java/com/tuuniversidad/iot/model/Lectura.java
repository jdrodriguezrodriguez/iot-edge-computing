package com.tuuniversidad.iot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "telemetria")
public class Lectura {

    @Id
    private String id;

    private Double temperatura;

    private String unidad = "Celsius";

    private LocalDateTime fecha = LocalDateTime.now();
}
