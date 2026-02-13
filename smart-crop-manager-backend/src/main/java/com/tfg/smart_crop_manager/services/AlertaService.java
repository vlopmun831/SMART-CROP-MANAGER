package com.tfg.smart_crop_manager.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.dto.AlertaDTO;
import com.tfg.smart_crop_manager.mappers.AlertaMapper;
import com.tfg.smart_crop_manager.persistence.entities.Alerta;
import com.tfg.smart_crop_manager.persistence.enums.EstadoAlerta;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta;
import com.tfg.smart_crop_manager.persistence.repositories.AlertaRepository;
import com.tfg.smart_crop_manager.services.exceptions.AlertaException;
import com.tfg.smart_crop_manager.services.exceptions.AlertaNotFoundException;

@Service
public class AlertaService {

	@Autowired
	private AlertaRepository alertaRepository;

	@Autowired
	private ZonaCultivoService zonaCultivoService;

	// Ver todas las alertas programadas (para una zona)
	public List<Alerta> findByZonaCultivoId(int idZona) {
		zonaCultivoService.findById(idZona);
		return this.alertaRepository.findByZonaCultivoIdOrderByFechaDesc(idZona);
	}
	public List<AlertaDTO> findPendientesByUsuario(Integer idUsuario) {
	    // 1. Buscamos las entidades en la base de datos
	    List<Alerta> alertas = alertaRepository.findPendientesByUsuarioId(idUsuario);
	    
	    // 2. Si no hay alertas, podemos devolver una lista vacía o lanzar una excepción según prefieras
	    if (alertas.isEmpty()) {
	        return new ArrayList<>();
	    }
	    
	    // 3. Convertimos a DTO usando el mapper (esto evita el JSON infinito)
	    return AlertaMapper.toDTOList(alertas);
	}
	public Alerta findById(int id) {
		if (!this.alertaRepository.existsById(id)) {
			throw new AlertaNotFoundException("El id de la alerta no existe.");
		}
		return this.alertaRepository.findById(id).get();
	}

	// Operaciones de Gestión

	// Crear una nueva alerta (normalmente generada automáticamente por un proceso)
	public Alerta create(Alerta alerta) {

		// Comprueba la estructura de los datos de entrada (el JSON/Body de la
		// petición).
		if (alerta.getZonaCultivo() == null || alerta.getZonaCultivo().getId() == null) {
			throw new AlertaException("La alerta debe estar asociada a una ZonaCultivo válida.");
		}
		if (alerta.getTipoAlerta() == null) {
			throw new AlertaException("Debe especificar el Tipo de Alerta (ej. TEMP_ALTA).");
		}

		// Se requiere min y max para definir el umbral que dispara la alerta.
		if (alerta.getMin() == null || alerta.getMax() == null) {
			throw new AlertaException("Debe especificar los valores min y max para definir el umbral.");
		}
		if (alerta.getMin() >= alerta.getMax()) {
			throw new AlertaException("El valor mínimo de la alerta debe ser estrictamente menor que el valor máximo.");
		}

		// Si la zona no existe, findById lanzará ZonaCultivoNotFoundException.
		zonaCultivoService.findById(alerta.getZonaCultivo().getId());

		alerta.setId(0); // Usar 0 para nuevo registro

		if (alerta.getEstado() == null) {
			alerta.setEstado(EstadoAlerta.PENDIENTE); // Una alerta nueva siempre se inicia PENDIENTE

		}

		// La fecha de detección se establece al momento de la creación
		if (alerta.getFecha() == null) {
		}

		// Guardamos alerta
		return this.alertaRepository.save(alerta);
	}

	// Requisito: Marcar alertas como resueltas
	public Alerta marcarComoResuelta(int idAlerta) {
		Alerta alertaBD = this.findById(idAlerta);

		if (alertaBD.getEstado().equals(EstadoAlerta.RESUELTA)) {
			throw new AlertaException("La alerta ya se encuentra en estado RESUELTA.");
		}

		alertaBD.setEstado(EstadoAlerta.RESUELTA);
		return this.alertaRepository.save(alertaBD);
	}

	public void deleteRuleByTipo(Integer idZona, TipoAlerta tipoAlerta) {

		zonaCultivoService.findById(idZona);

		// Buscar la Alerta/Regla específica por Zona y Tipo usando List
		List<Alerta> reglasExistentes = this.alertaRepository.findByZonaCultivoIdAndTipoAlerta(idZona, tipoAlerta);

		if (!reglasExistentes.isEmpty()) {
			// 2. Si la regla existe, eliminar el objeto encontrado
			this.alertaRepository.delete(reglasExistentes.get(0));
		} else {
			// 3. Si no existe, lanzar la excepción apropiada
			throw new AlertaNotFoundException(String.format(
					"No se encontró ninguna regla de alerta de tipo %s para la zona %d.", tipoAlerta.name(), idZona));
		}
	}

	// Este método lo mantenemos para eliminar por ID directo (ej. por parte del
	// ADMIN)

	public void delete(int id) {
		if (!this.alertaRepository.existsById(id)) {
			throw new AlertaNotFoundException("El id de la alerta no existe.");
		}
		this.alertaRepository.deleteById(id);
	}

}
