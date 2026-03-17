package com.tfg.smart_crop_manager.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.dto.LoginRequest;
import com.tfg.smart_crop_manager.dto.LoginResponse;
import com.tfg.smart_crop_manager.dto.RefreshDTO;
import com.tfg.smart_crop_manager.dto.RegisterRequest;
import com.tfg.smart_crop_manager.services.AuthService;

@RestController
@RequestMapping("/auth") // Todas las rutas empezarán por http://localhost:8080/auth
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	// 1. Ruta para entrar: /auth/login
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(this.authService.login(request));
	}

	// 2. Ruta para nuevos agricultores: /auth/register
	@PostMapping("/register")
	public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(this.authService.registrar(request));
	}
	
	// 3. Ruta para renovar el token: /auth/refresh
	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshDTO request) {
		// Nota: Asegúrate de que tu AuthService tenga el método refresh implementado
		return ResponseEntity.ok(this.authService.refresh(request));
	}
} 