package com.tfg.smart_crop_manager.web.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtFilter jwtFilter;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 1. Rutas públicas (Login y Registro)
				.requestMatchers("/auth/**").permitAll()
				
				// 2. ÁREA DE ADMINISTRACIÓN: Solo el jefe (ADMIN)
			    // Solo el admin puede listar, crear, modificar o borrar usuarios y administradores
			    .requestMatchers("/usuario/**").hasRole("ADMIN")
			    .requestMatchers("/administrador/**").hasRole("ADMIN")

			    // 3. ÁREA DE CULTIVOS (Zonas): Compartida con permisos específicos
			    // Ver todas las zonas: solo Admin
			    .requestMatchers(HttpMethod.GET, "/zonas").hasRole("ADMIN") 
			    
			    // Operaciones de gestión de zonas: Admin y Usuario
			    .requestMatchers("/zonas/**").hasAnyRole("ADMIN", "USUARIO")

			    // 4. ÁREA DE DATOS Y SENSORES (Registros):
			    // El sensor (POST) y las consultas las pueden hacer ambos
			    .requestMatchers("/registros/**").hasAnyRole("ADMIN", "USUARIO")

			    // 5. ÁREA DE OPERACIONES (Riego y Alertas):
			    // El agricultor necesita gestionar sus riegos y alertas
			    .requestMatchers("/riego/**").hasAnyRole("ADMIN", "USUARIO")
			    .requestMatchers("/alertas/**").hasAnyRole("ADMIN", "USUARIO")

			    // 6. SEGURIDAD TOTAL: Cualquier otra ruta pide estar logueado
			    .anyRequest().authenticated()
			)
			// Añadimos tu vigilante JWT antes del filtro de usuario/password
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
			
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitimos el acceso desde el futuro Frontend (Angular/React)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }	
}