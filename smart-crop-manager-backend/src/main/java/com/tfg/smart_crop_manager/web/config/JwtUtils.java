package com.tfg.smart_crop_manager.web.config;


import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JwtUtils {
	
	private static String SECRET_KEY = "mi_clave_secreta_tfg_2026";
	private static Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
	
	
	public String create (String email) {
		return JWT.create()
				.withSubject(email)
				.withIssuer("smartcrop")
				.withIssuedAt(new Date())
				.withExpiresAt(new Date (System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(15)))
				.sign(ALGORITHM);
	}

	
	public String getEmail(String jwt) {
        return JWT.require(ALGORITHM).build().verify(jwt).getSubject();
    }

    public boolean isValid(String jwt) {
        try {
            JWT.require(ALGORITHM).build().verify(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    
}