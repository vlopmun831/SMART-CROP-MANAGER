package com.tfg.smart_crop_manager.mappers;

import com.tfg.smart_crop_manager.dto.RiegoDTO;
import com.tfg.smart_crop_manager.persistence.entities.Riego;

public class RiegoMapper {
	public static RiegoDTO toDTO(Riego entity) {
		if (entity == null)
			return null;
		RiegoDTO dto = new RiegoDTO();
		dto.setId(entity.getId());
		dto.setHoraInicio(entity.getHoraInicio());
		dto.setHoraFin(entity.getHoraFin());

		if (entity.getZonaCultivo() != null) {
			dto.setNombreZona(entity.getZonaCultivo().getUbicacion());
		}
		return dto;
	}

}
