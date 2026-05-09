package com.tfg.smart_crop_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
	private String token;
	private String email;
	private String rol;
	private String nombre;
	private Integer id;

}
