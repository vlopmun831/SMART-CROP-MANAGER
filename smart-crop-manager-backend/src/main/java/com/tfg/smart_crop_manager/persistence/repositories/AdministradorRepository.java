package com.tfg.smart_crop_manager.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.smart_crop_manager.persistence.entities.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer>{
	// Método para buscar un administrador por su email para el login
    List<Administrador> findByEmail(String email);

}
