package com.tfg.smart_crop_manager.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegistroDTO {
	
	private Integer id;
	
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fecha;
	
    private Double temperatura;
    
    private Double humedadSuelo;
    
    private Double humedadAire;
    
    private boolean lluvia;
    
    // Solo info necesaria de la zona para evitar recursividad
    private Integer idZona;
    private String ubicacionZona;
	

}
