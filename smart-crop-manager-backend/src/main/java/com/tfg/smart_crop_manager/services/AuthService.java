package com.tfg.smart_crop_manager.services;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Importa tus DTOs aquí (asegúrate de que los nombres coincidan)
import com.tfg.smart_crop_manager.dto.LoginRequest;
import com.tfg.smart_crop_manager.dto.LoginResponse;
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
        // 1. Spring Security comprueba email y password
        // IMPORTANTE: UsernamePasswordAuthenticationToken es de Spring Security
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        
        // El principal es el UserDetails que crearemos en el paso 3
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 2. Generamos los tokens con el JwtUtils de tu profe
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        LoginResponse response = new LoginResponse();
        response.setAccess(accessToken);
        response.setRefresh(refreshToken);

        return response;
    }
    
    public LoginResponse registrar(RegisterRequest request) {
        if(!request.getPassword1().equals(request.getPassword2())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getUsername());
        nuevoUsuario.setPassword(request.getPassword1()); 
        nuevoUsuario.setRol(Rol.USUARIO);
        nuevoUsuario.setNombre(request.getNombre());
        
        usuarioService.create(nuevoUsuario);

        return login(new LoginRequest(request.getUsername(), request.getPassword1()));
    }
}
