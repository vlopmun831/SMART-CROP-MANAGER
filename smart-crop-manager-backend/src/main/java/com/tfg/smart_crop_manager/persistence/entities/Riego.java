package com.tfg.smart_crop_manager.persistence.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "riego")
@Getter
@Setter
public class Riego {
	
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    private LocalDate fecha;

	    @Column(name = "hora_inicio") 
	    private LocalDate horaInicio;
	    
	    @Column(name = "hora_fin") 
	    private LocalDate horaFin;
	    
	    
	    
	 // Relación N:1 con ZonaCultivo
	    @ManyToOne
	    @JoinColumn(name = "id_zona", nullable = false) // Mapeo a IdZona: Integer
	    private ZonaCultivo zonaCultivo;
}

