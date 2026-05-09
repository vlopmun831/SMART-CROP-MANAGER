package com.tfg.smart_crop_manager.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.dto.LoginDTO;
import com.tfg.smart_crop_manager.dto.RegisterDTO;
import com.tfg.smart_crop_manager.dto.TokenDTO;
import com.tfg.smart_crop_manager.persistence.repositories.UsuarioRepository;
import com.tfg.smart_crop_manager.services.AuthService;
import com.tfg.smart_crop_manager.web.config.JwtUtils;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth") // Todas las rutas empezarán por http://localhost:8080/auth
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDto) {
		try {
			TokenDTO tokenDto = authService.login(loginDto);
			return ResponseEntity.ok(tokenDto);
		} catch (Exception e) {
			// Si las credenciales son malas, saltará una excepción
			return ResponseEntity.status(401).body("Error: Email o contraseña incorrectos");
		}
	}

	// 2. Ruta para nuevos agricultores: /auth/register
	@PostMapping("/register")
	public ResponseEntity<?> registrar(@RequestBody RegisterDTO registerDto) {
		try {
			TokenDTO tokenDto = authService.registrar(registerDto);
			return ResponseEntity.ok(tokenDto);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: El email ya está en uso.");
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(Authentication authentication) {
		// Authentication contiene los datos del usuario logueado actualmente
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no válido o expirado");
		}

		// authentication.getName() nos da el email del usuario del token actual
		TokenDTO newToken = authService.refresh(authentication.getName());

		return ResponseEntity.ok(newToken);
	}
}