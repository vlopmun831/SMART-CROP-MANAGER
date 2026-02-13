package com.tfg.smart_crop_manager.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.tfg.smart_crop_manager.dto.RegistroDTO;
import com.tfg.smart_crop_manager.persistence.entities.Registro;

public class RegistroMapper {
	
	public static RegistroDTO toDTO(Registro registro) {
        if (registro == null) return null;

        RegistroDTO dto = new RegistroDTO();

        dto.setId(registro.getId());
        dto.setFecha(registro.getFecha());
        dto.setTemperatura(registro.getTemperatura());
        dto.setHumedadSuelo(registro.getHumedadSuelo());
        dto.setHumedadAire(registro.getHumedadAire());
        dto.setLluvia(registro.isLluvia());

        // Extraemos datos de la zona relacionada
        if (registro.getZonaCultivo() != null) {
            dto.setIdZona(registro.getZonaCultivo().getId());
            dto.setUbicacionZona(registro.getZonaCultivo().getUbicacion());
        }

        return dto;
    }

    public static List<RegistroDTO> toDTOsFuncional(List<Registro> registros) {
        return registros.stream()
                .map(r -> RegistroMapper.toDTO(r))
                .collect(Collectors.toList());
    }

}
