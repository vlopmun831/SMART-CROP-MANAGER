package com.tfg.smart_crop_manager.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({ "id", "nombre", "email", "numZonas", "listaZonas"})
public class UsuarioDTO {
	
	
	private Integer id;
    private String nombre;
    private String email;
    private List<ZonaCultivoDTO>listaZonas;
    // Ponemos un contador de zonas del usuario
    private int numZonas;
}
