package com.tfg.smart_crop_manager.persistence.enums;

public enum TipoAlerta {

	  // SUELO

    SUELO_SECO,           // Falta agua urgente

    SUELO_ENCHARCADO,     // Exceso de agua (riesgo de pudrición)

    // AMBIENTE

    CALOR_EXTREMO,        // Evita regar a mediodía o indica estrés

    AIRE_MUY_SECO,        // La planta pierde agua por las hojas muy rápido

    RIESGO_HONGOS,        // Humedad del aire muy alta por mucho tiempo

    // EXTERNOS

    PRONOSTICO_LLUVIA,    // Para ahorrar agua y no regar si va a llover

    FALLO_SENSOR          // Por si un cable se suelta o el sensor falla

}	
	

