package com.tfg.smart_crop_manager.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;

public interface ZonaCultivoRepository extends JpaRepository<ZonaCultivo, Integer>{
	// Método para encontrar todas las zonas gestionadas por un usuario
    List<ZonaCultivo> findByUsuarioId(Integer idUsuario);
}


