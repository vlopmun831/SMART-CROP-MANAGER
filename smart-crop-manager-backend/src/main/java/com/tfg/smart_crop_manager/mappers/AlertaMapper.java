package com.tfg.smart_crop_manager.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.tfg.smart_crop_manager.dto.AlertaDTO;
import com.tfg.smart_crop_manager.persistence.entities.Alerta;

public class AlertaMapper {
	
	public static AlertaDTO toDTO(Alerta alerta) {
		if (alerta == null) return null;

        AlertaDTO dto = new AlertaDTO();
        dto.setId(alerta.getId());
		dto.setTipo(alerta.getTipoAlerta()); 
        dto.setEstado(alerta.getEstado());
        
        dto.setFecha(alerta.getFecha());
        dto.setMax(alerta.getMax());
        dto.setMin(alerta.getMin());

        if (alerta.getZonaCultivo() != null) {
            dto.setIdZona(alerta.getZonaCultivo().getId());
            dto.setNombreZona(alerta.getZonaCultivo().getUbicacion());
            if (alerta.getZonaCultivo().getUsuario() != null) {
                dto.setNombreUsuario(alerta.getZonaCultivo().getUsuario().getNombre());
            } else {
                dto.setNombreUsuario("Sin asignar");
            }
        
        
        }

        return dto;
    }

    public static List<AlertaDTO> toDTOList(List<Alerta> alertas) {
        return alertas.stream()
                      .map(AlertaMapper::toDTO)
                      .collect(Collectors.toList());
    }
	    
}
