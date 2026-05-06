package com.tfg.smart_crop_manager.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.smart_crop_manager.persistence.entities.Riego;

public interface RiegoRepository extends JpaRepository<Riego, Integer>{

	// Consultar el historial de riego de una zona (Requisito de Usuario)
    List<Riego> findByZonaCultivoIdOrderByFechaDescHoraInicioDesc(Integer idZona);
    List<Riego> findByZonaCultivoIdAndHoraFinIsNull(Integer idZona);
    
}
