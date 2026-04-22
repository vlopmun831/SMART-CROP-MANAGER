package com.tfg.smart_crop_manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.dto.LoginDTO;
import com.tfg.smart_crop_manager.dto.RegisterDTO;
import com.tfg.smart_crop_manager.dto.TokenDTO;
import com.tfg.smart_crop_manager.persistence.entities.Usuario;
import com.tfg.smart_crop_manager.persistence.repositories.UsuarioRepository;
import com.tfg.smart_crop_manager.web.config.JwtUtils;

@Service
public class AuthService {

	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired 
    @Lazy
    private PasswordEncoder passwordEncoder;

    public TokenDTO login(LoginDTO loginDto) {
        // 1. Intentar autenticar con email y password
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // 2. Si llegamos aquí sin errores, la autenticación fue exitosa
        Usuario usuario = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado post-autenticación"));

        // 3. Generar el token
        String token = jwtUtils.create(usuario.getEmail());

        // 4. Devolver el DTO con los datos que necesita el frontend
        return new TokenDTO(token, usuario.getEmail(), usuario.getRol().name(),usuario.getNombre(),
                usuario.getId());
    }
    
 // DENTRO de AuthService.java
    public TokenDTO registrar(RegisterDTO registerDto) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerDto.getNombre());
        nuevoUsuario.setEmail(registerDto.getEmail());
        
        // Encriptamos la contraseña (esto es lo que pedía tu PDF [cite: 363, 380])
        nuevoUsuario.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        nuevoUsuario.setRol(registerDto.getRol());

        // Guardamos en la base de datos
        usuarioRepository.save(nuevoUsuario);

        // Generamos el token para que el usuario entre directamente [cite: 559, 593]
        String token = jwtUtils.create(nuevoUsuario.getEmail());
        return new TokenDTO(token, nuevoUsuario.getEmail(), nuevoUsuario.getRol().name(), nuevoUsuario.getNombre(), nuevoUsuario.getId());
    }

    public TokenDTO refresh(String email) {
        // Simplemente generamos un nuevo token para el usuario que ya está autenticado
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        String nuevoToken = jwtUtils.create(usuario.getEmail());
        return new TokenDTO(nuevoToken, usuario.getEmail(), usuario.getRol().name(),usuario.getNombre(), usuario.getId());
    }
}