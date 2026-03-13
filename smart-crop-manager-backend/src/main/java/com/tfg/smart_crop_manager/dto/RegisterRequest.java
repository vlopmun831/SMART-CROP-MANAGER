package com.tfg.smart_crop_manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
	
	
	private String email;  // Email
    private String password1; // Contraseña
    private String password2; // Repetir contraseña para validar
    private String nombre;
}
