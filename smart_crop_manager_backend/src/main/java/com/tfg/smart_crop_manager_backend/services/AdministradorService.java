package com.tfg.smart_crop_manager_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager_backend.persistence.entities.Administrador;
import com.tfg.smart_crop_manager_backend.persistence.repositories.AdministradorRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    public List<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    public Administrador findById(int id) {
    	
    	if(!this.adminitradorRepository.existsById(id)) {
			throw new AdministradorNotFoundException("El ID indicado no existe. ");
		}
		
	}
        return administradorRepository.findById(id);
    }

    public Administrador save(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    public void deleteById(Integer id) {
        administradorRepository.deleteById(id);
    }
}



