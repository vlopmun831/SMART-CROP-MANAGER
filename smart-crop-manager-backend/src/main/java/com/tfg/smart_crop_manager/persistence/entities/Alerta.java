package com.tfg.smart_crop_manager.persistence.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfg.smart_crop_manager.persistence.enums.EstadoAlerta;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alerta")
@Getter
@Setter
@NoArgsConstructor
public class Alerta {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Enumerated(EnumType.STRING)
	    private TipoAlerta tipoAlerta;
	    
	    private String descripcion;

	    private Double max;
	    private Double min;

	    @Enumerated(EnumType.STRING)
	    private EstadoAlerta estado;
	    
	    
	    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	    private LocalDateTime fecha;
	
	 // Relación N:1 con ZonaCultivo
	    @ManyToOne
	    @JoinColumn(name = "id_zona", nullable = false)
	    private ZonaCultivo zonaCultivo;
}
