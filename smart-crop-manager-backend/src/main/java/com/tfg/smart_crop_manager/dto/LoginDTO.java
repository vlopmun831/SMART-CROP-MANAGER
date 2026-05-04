package com.tfg.smart_crop_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDTO {
	
	@NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
	private String email;
    private String password;

}
