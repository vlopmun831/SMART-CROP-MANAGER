package com.tfg.smart_crop_manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.dto.LoginRequest;
import com.tfg.smart_crop_manager.dto.LoginResponse;
import com.tfg.smart_crop_manager.dto.RefreshDTO; // 👈 Importante añadir este
import com.tfg.smart_crop_manager.dto.RegisterRequest;
import com.tfg.smart_crop_manager.persistence.entities.Usuario;
import com.tfg.smart_crop_manager.persistence.enums.Rol;
import com.tfg.smart_crop_manager.web.config.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    public LoginResponse login(LoginRequest request) {
        // 1. Validamos credenciales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 2. Generamos los dos tokens
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse registrar(RegisterRequest request) {
        if (!request.getPassword1().equals(request.getPassword2())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        Usuario nuevo = new Usuario();
        nuevo.setEmail(request.getEmail());
        nuevo.setNombre(request.getNombre());
        nuevo.setPassword(request.getPassword1()); 
        nuevo.setRol(Rol.USUARIO);

        usuarioService.create(nuevo);

        return login(new LoginRequest(request.getEmail(), request.getPassword1()));
    }

    public LoginResponse refresh(RefreshDTO request) {
        // Extraemos el email del token de refresco
        String email = jwtUtil.extractUsername(request.getRefresh());
        
        // Cargamos los detalles del usuario
        UserDetails userDetails = usuarioService.loadUserByUsername(email);
        
        // Validamos el token y generamos nuevos si es correcto
        if (jwtUtil.validateToken(request.getRefresh(), userDetails)) {
            String newAccess = jwtUtil.generateAccessToken(userDetails);
            String newRefresh = jwtUtil.generateRefreshToken(userDetails);
            
            return new LoginResponse(newAccess, newRefresh);
        } else {
            throw new RuntimeException("Token de refresco inválido");
        }
    }
}