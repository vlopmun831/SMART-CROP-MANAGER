package com.tfg.smart_crop_manager.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tfg.smart_crop_manager.dto.UsuarioDTO;
import com.tfg.smart_crop_manager.persistence.entities.Usuario;

public class UsuarioMapper {

	public static UsuarioDTO toDTO(Usuario usuario) {
		if (usuario == null)
			return null;

		UsuarioDTO dto = new UsuarioDTO();

		dto.setId(usuario.getId());
		dto.setNombre(usuario.getNombre());
		dto.setEmail(usuario.getEmail());

		// Contamos cuántas zonas tiene
		if (usuario.getZonasCultivo() != null) {
			dto.setNumZonas(usuario.getZonasCultivo().size());
		}

		if (usuario.getZonasCultivo() != null) {
			dto.setListaZonas(ZonaCultivoMapper.toDTOsFuncional(usuario.getZonasCultivo()));
		}

		return dto;
	}

	public static List<UsuarioDTO> toDTOsFuncional(List<Usuario> usuarios) {
		return usuarios.stream().map(u -> UsuarioMapper.toDTO(u)).collect(Collectors.toList());
	}

	public static List<UsuarioDTO> toDTOsDeclarativo(List<Usuario> usuarios) {
		List<UsuarioDTO> dtos = new ArrayList<>();
		for (Usuario u : usuarios) {
			dtos.add(UsuarioMapper.toDTO(u));
		}
		return dtos;
	}
}
