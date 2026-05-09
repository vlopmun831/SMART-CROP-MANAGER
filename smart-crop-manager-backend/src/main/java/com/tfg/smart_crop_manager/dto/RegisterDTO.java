package com.tfg.smart_crop_manager.dto;

import com.tfg.smart_crop_manager.persistence.enums.Rol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterDTO {

	private String nombre;
	private String email;
	private String password;
	private Rol rol; // Para que elijas si nace como ADMIN o USUARIO
}
