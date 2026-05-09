package com.tfg.smart_crop_manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SensorPayloadDTO {

	// El sensor solo sabe su ID de zona, no manda el objeto Zona entero
	private Integer idZona;

	// Las lecturas directas de los pines del Arduino
	private Double temperatura;
	private Double humedadSuelo;
	private boolean lluvia;

	// Clave de seguridad para que nadie envíe datos falsos a tu API
	private String apiKey;

}
