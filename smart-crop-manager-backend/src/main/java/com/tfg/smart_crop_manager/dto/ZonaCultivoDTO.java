package com.tfg.smart_crop_manager.dto;

import java.time.LocalDateTime;

import com.tfg.smart_crop_manager.persistence.enums.VariedadCultivo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class ZonaCultivoDTO {
	
	private Integer id;
    private VariedadCultivo varCultivo;
    private String ubicacion;
    
    //Nos traemos también el nombre del usuario y su id
    private String nombreUsuario; 
    private Integer idUsuario;
	
    
    private Double ultimaTemperatura;
    private Double ultimaHumedadSuelo;
    private LocalDateTime fechaUltimaLectura;

}
