package com.tfg.smart_crop_manager.persistence.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.smart_crop_manager.persistence.entities.Registro;

public interface RegistroRepository extends JpaRepository<Registro, Integer> {

	// Consultar el historial de registros de una zona específica
	List<Registro> findByZonaCultivoIdOrderByFechaDesc(Integer idZona);

	// Consultar registros entre dos fechas (útil para gráficos de evolución)
	List<Registro> findByZonaCultivoIdAndFechaBetween(Integer idZona, LocalDate fechaInicio, LocalDate fechaFin);

	// Consultar el último registro de una zona
	List<Registro> findTop1ByZonaCultivoIdOrderByFechaDesc(Integer idZona);

}
