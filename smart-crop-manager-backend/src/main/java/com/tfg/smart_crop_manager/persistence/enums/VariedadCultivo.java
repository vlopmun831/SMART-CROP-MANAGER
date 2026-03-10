package com.tfg.smart_crop_manager.persistence.enums;

import lombok.Getter;

@Getter
public enum VariedadCultivo {
	
	// Ejemplo: VARIEDAD(HumMin, HumMax, TempMax, HumAireMax)
    VARIEDAD_1(30.0, 80.0, 35.0, 70.0), 
    VARIEDAD_2(15.0, 90.0, 45.0, 60.0), 
    VARIEDAD_3(50.0, 95.0, 28.0, 85.0);

    private final double humSueloMin; // Para SUELO_SECO
    private final double humSueloMax; // Para SUELO_ENCHARCADO
    private final double tempMax;     // Para CALOR_EXTREMO
    private final double humAireMax;  // Para RIESGO_HONGOS

    VariedadCultivo(double humSueloMin, double humSueloMax, double tempMax, double humAireMax) {
        this.humSueloMin = humSueloMin;
        this.humSueloMax = humSueloMax;
        this.tempMax = tempMax;
        this.humAireMax = humAireMax;
    }
}


