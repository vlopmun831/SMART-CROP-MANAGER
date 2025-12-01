package com.tfg.smart_crop_manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager_backend.persistence.entities.Administrador;
import com.tfg.smart_crop_manager_backend.persistence.repositories.AdministradorRepository;


@Service
public class AdministradorService {
	
	@Autowired
	private AdministradorRepository administradorRepository;
	
	
	public Administrador create(Administrador administrador) {


		administrador.setId(0);

		return this.administradorRepository.save(administrador);

	}
	
	
}
