package com.tfg.smart_crop_manager.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tfg.smart_crop_manager.dto.ZonaCultivoDTO;
import com.tfg.smart_crop_manager.persistence.entities.Registro;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;

public class ZonaCultivoMapper {
	
	public static ZonaCultivoDTO toDTO(ZonaCultivo zona) {
        if (zona == null) return null;

        ZonaCultivoDTO dto = new ZonaCultivoDTO();

        dto.setId(zona.getId());
        dto.setVarCultivo(zona.getVarCultivo());
        dto.setUbicacion(zona.getUbicacion());

        // Aplanamos la relación con Usuario para evitar el JSON infinito
        if (zona.getUsuario() != null) {
            dto.setNombreUsuario(zona.getUsuario().getNombre());
            dto.setIdUsuario(zona.getUsuario().getId());
        }
    

	// (Lógica para el resumen)
    if (zona.getRegistros() != null && !zona.getRegistros().isEmpty()) {
        // Cogemos el último registro (el más reciente)
        Registro ultimo = zona.getRegistros().get(zona.getRegistros().size() - 1);
        
        dto.setUltimaTemperatura(ultimo.getTemperatura());
        dto.setUltimaHumedadSuelo(ultimo.getHumedadSuelo());
        dto.setFechaUltimaLectura(ultimo.getFecha());
    }

    return dto;
	}
	
    
    public static List<ZonaCultivoDTO> toDTOsFuncional(List<ZonaCultivo> zonas) {
        return zonas.stream()
                .map(z -> ZonaCultivoMapper.toDTO(z))
                .collect(Collectors.toList());
    }

    public static List<ZonaCultivoDTO> toDTOsDeclarativo(List<ZonaCultivo> zonas) {
        List<ZonaCultivoDTO> dtos = new ArrayList<>();
        for (ZonaCultivo z : zonas) {
            dtos.add(ZonaCultivoMapper.toDTO(z));
        }
        return dtos;
    }

}
