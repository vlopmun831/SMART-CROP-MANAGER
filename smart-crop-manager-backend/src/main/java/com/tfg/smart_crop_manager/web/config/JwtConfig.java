package com.tfg.smart_crop_manager.web.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;
	@Getter
	@Setter
	public class JwtConfig {
		@Value("${jwt.secret}")
	    private String secret;

	    @Value("${jwt.access.expires}")
	    private long accessTokenExpires;

	    @Value("${jwt.refresh.expires}")
	    private long refreshTokenExpires;

	    @Value("${jwt.issuer}")
	    private String issuer;
	}

