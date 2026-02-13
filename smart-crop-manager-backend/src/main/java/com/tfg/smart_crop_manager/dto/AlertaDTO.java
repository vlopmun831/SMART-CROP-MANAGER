package com.tfg.smart_crop_manager.dto;

import java.time.LocalDateTime;

import com.tfg.smart_crop_manager.persistence.enums.EstadoAlerta;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlertaDTO {
	private Integer id;
    private TipoAlerta tipo;
    private EstadoAlerta estado;
    private Double max;
    private Double min;
    private LocalDateTime fecha;
    
    
    // Solo enviamos el ID y quizás el nombre de la zona para el agricultor

    private Integer idZona;
    private String nombreZona;
    
 
}


