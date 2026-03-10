package com.tfg.smart_crop_manager.persistence.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "registro")
@Getter
@Setter
@NoArgsConstructor
public class Registro {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	 	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	    private LocalDateTime fecha;

	    private Double temperatura;
	    
	    @Column(name = "humedad_suelo") 
	    private Double humedadSuelo;
	    
	    @Column(name = "humedad_aire") 
	    private Double humedadAire;
	    
	    
	    private boolean lluvia;
	    
	    
	 // Relación N:1 con ZonaCultivo
	    @ManyToOne
	    @JoinColumn(name = "id_zona", nullable = false) // Mapeo a IdZona: Integer
	    private ZonaCultivo zonaCultivo;

}
