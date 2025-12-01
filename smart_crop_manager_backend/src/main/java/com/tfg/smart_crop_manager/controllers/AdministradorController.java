package com.tfg.smart_crop_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.services.AdministradorService;
import com.tfg.smart_crop_manager_backend.persistence.entities.Administrador;

@RestController
@RequestMapping("/administrador")

public class AdministradorController {
	
	@Autowired
	private AdministradorService administradorService;
	
	
	 @PostMapping
	    public Administrador createCliente(@RequestBody Administrador administrador) {
	        return administradorService.create(administrador);
	    }


}
