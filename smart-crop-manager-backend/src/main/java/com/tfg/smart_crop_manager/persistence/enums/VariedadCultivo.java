package com.tfg.smart_crop_manager.persistence.enums;

import lombok.Getter;

@Getter
public enum VariedadCultivo {

	// Ejemplo: VARIEDAD(HumMin, HumMax, TempMax)
	TOMATE(40.0, 80.0, 32.0), // Necesita humedad constante y clima templado
	OLIVO(20.0, 65.0, 45.0), // Muy resistente a la sequía y al calor extremo
	ALMENDRO(25.0, 75.0, 40.0), // Requiere mas agua
	VID(30.0, 75.0, 38.0); // Equilibrio: aguanta calor pero necesita control de humedad

	private final double humSueloMin; // Para SUELO_SECO
	private final double humSueloMax; // Para SUELO_ENCHARCADO
	private final double tempMax; // Para CALOR_EXTREMO

	VariedadCultivo(double humSueloMin, double humSueloMax, double tempMax) {
		this.humSueloMin = humSueloMin;
		this.humSueloMax = humSueloMax;
		this.tempMax = tempMax;
	}
}
