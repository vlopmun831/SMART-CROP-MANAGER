package com.tfg.smart_crop_manager.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tfg.smart_crop_manager.persistence.entities.Alerta;
import com.tfg.smart_crop_manager.persistence.enums.EstadoAlerta;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta;

public interface AlertaRepository extends JpaRepository<Alerta, Integer>{
	// Requisito: Ver todas las alertas programadas (por zona)
    List<Alerta> findByZonaCultivoIdOrderByFechaDesc(Integer idZona);
    
    // Requisito: Ver alertas pendientes
    List<Alerta> findByZonaCultivoIdAndEstado(Integer idZona, EstadoAlerta estado);
	
    //Requisito:borrar tipo alerta
    List<Alerta>findByZonaCultivoIdAndTipoAlerta(Integer idZona, TipoAlerta tipoAlerta);
    
    List<Alerta> findByZonaCultivoIdAndTipoAlertaAndEstado(Integer idZona, TipoAlerta tipo, EstadoAlerta estado);
    
 // AlertaRepository.java
 // Reemplaza tu método findPendientesByUsuarioId por este:
    @Query("SELECT a FROM Alerta a WHERE a.zonaCultivo.usuario.id = :idUsuario AND a.estado = com.tfg.smart_crop_manager.persistence.enums.EstadoAlerta.PENDIENTE")
    List<Alerta> findPendientesByUsuarioId(@Param("idUsuario") Integer idUsuario);
}