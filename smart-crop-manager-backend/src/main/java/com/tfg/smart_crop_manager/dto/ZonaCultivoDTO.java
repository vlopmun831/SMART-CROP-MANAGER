package com.tfg.smart_crop_manager.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class ZonaCultivoDTO {

	private Integer id;
	private String varCultivo;
	private String ubicacion;
	
	private Double humSueloMinConfig;
	private Double humSueloMaxConfig;
	private Double tempMaxConfig;


	// Nos traemos también el nombre del usuario y su id
	private String nombreUsuario;
	private Integer idUsuario;

	private Double ultimaTemperatura;
	private Double ultimaHumedadSuelo;
	private LocalDateTime fechaUltimaLectura;

}
