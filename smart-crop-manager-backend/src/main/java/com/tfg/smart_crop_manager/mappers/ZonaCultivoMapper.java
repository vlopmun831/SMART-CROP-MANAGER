package com.tfg.smart_crop_manager.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tfg.smart_crop_manager.dto.ZonaCultivoDTO;
import com.tfg.smart_crop_manager.persistence.entities.Registro;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;

public class ZonaCultivoMapper {

	public static ZonaCultivoDTO toDTO(ZonaCultivo zona) {
		if (zona == null)
			return null;
		ZonaCultivoDTO dto = new ZonaCultivoDTO(); // Solo una vez aquí
		dto.setId(zona.getId());
		dto.setVarCultivo(zona.getVarCultivo());
		dto.setUbicacion(zona.getUbicacion());
		// Relación con Usuario
		if (zona.getUsuario() != null) {
			dto.setIdUsuario(zona.getUsuario().getId());
			dto.setNombreUsuario(zona.getUsuario().getNombre());
		} else {
			dto.setIdUsuario(null);
			dto.setNombreUsuario("ZONA DISPONIBLE");
		}
		// Lógica para el resumen (Última lectura)
		if (zona.getRegistros() != null && !zona.getRegistros().isEmpty()) {
			Registro ultimo = zona.getRegistros().stream().filter(r -> r.getFecha() != null)
					.max((r1, r2) -> r1.getFecha().compareTo(r2.getFecha()))
					.orElse(zona.getRegistros().get(zona.getRegistros().size() - 1));

			dto.setUltimaTemperatura(ultimo.getTemperatura());
			dto.setUltimaHumedadSuelo(ultimo.getHumedadSuelo());
			dto.setFechaUltimaLectura(ultimo.getFecha());
		}
		// TUS NUEVAS LÍNEAS (Perfectas)
		dto.setHumSueloMinConfig(zona.getHumSueloMinConfig());
		dto.setHumSueloMaxConfig(zona.getHumSueloMaxConfig());
		dto.setTempMaxConfig(zona.getTempMaxConfig());

		return dto;
	}

	public static List<ZonaCultivoDTO> toDTOsFuncional(List<ZonaCultivo> zonas) {
		return zonas.stream().map(z -> ZonaCultivoMapper.toDTO(z)).collect(Collectors.toList());
	}

	public static List<ZonaCultivoDTO> toDTOsDeclarativo(List<ZonaCultivo> zonas) {
		List<ZonaCultivoDTO> dtos = new ArrayList<>();
		for (ZonaCultivo z : zonas) {
			dtos.add(ZonaCultivoMapper.toDTO(z));
		}
		return dtos;
	}

}
