package com.tfg.smart_crop_manager.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.dto.AlertaDTO;
import com.tfg.smart_crop_manager.mappers.AlertaMapper;
import com.tfg.smart_crop_manager.persistence.entities.Alerta;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
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

		// 2. Si no hay alertas, podemos devolver una lista vacía o lanzar una excepción
		// según prefieras
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

	// Marcar alertas como resueltas
	public Alerta marcarComoResuelta(int idAlerta) {
		Alerta alertaBD = this.findById(idAlerta);

		if (alertaBD.getEstado().equals(EstadoAlerta.RESUELTA)) {
			throw new AlertaException("La alerta ya se encuentra en estado RESUELTA.");
		}

		alertaBD.setEstado(EstadoAlerta.RESUELTA);
		return this.alertaRepository.save(alertaBD);
	}
	
	//Marcar como ignoradas
	public Alerta marcarComoIgnorada(Integer idAlerta) throws AlertaNotFoundException {
	    // 1. Buscamos la alerta en la base de datos
	    Alerta alertaBD= alertaRepository.findById(idAlerta)
	            .orElseThrow(() -> new AlertaNotFoundException("No existe la alerta con ID: " + idAlerta));
	    // 2. Cambiamos el estado (Asegúrate de que tu Enum EstadoAlerta tenga el valor IGNORADA)
	    alertaBD.setEstado(EstadoAlerta.IGNORADA);
	    
	    
	    // 4. Guardamos los cambios
	    return alertaRepository.save(alertaBD);
	}

	public List<AlertaDTO> findAllAlertas() {
		// 1. Buscamos todas las alertas de la base de datos (puedes usar findByEstado
		// si solo quieres las PENDIENTES)
		List<Alerta> alertas = alertaRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha"));

		// 2. Si está vacía devolvemos lista vacía
		if (alertas.isEmpty()) {
			return new ArrayList<>();
		}

		// 3. Usamos tu mapper para convertir todas a DTO de golpe
		return AlertaMapper.toDTOList(alertas);
	}

	// --- MÉTODO PARA EL SISTEMA (AUTOMÁTICO) ---

	public void registrarAlertaAutomatica(ZonaCultivo zona, TipoAlerta tipo, String mensaje) {
		Alerta alerta = new Alerta();
		alerta.setZonaCultivo(zona);
		alerta.setTipoAlerta(tipo);
		alerta.setDescripcion(mensaje);
		alerta.setFecha(LocalDateTime.now());
		alerta.setEstado(EstadoAlerta.PENDIENTE);

		// Inicializamos umbrales a 0 o null si tu entidad los obliga
		alerta.setMin(0.0);
		alerta.setMax(0.0);

		this.alertaRepository.save(alerta);
	}

	public void resolverAlertaPorZonaYTipo(Integer idZona, TipoAlerta tipo) {
		// Buscamos las alertas pendientes de esa zona y de ese tipo específico
		List<Alerta> alertasPendientes = alertaRepository.findByZonaCultivoIdAndTipoAlertaAndEstado(idZona, tipo,
				EstadoAlerta.PENDIENTE);

		for (Alerta alerta : alertasPendientes) {
			alerta.setEstado(EstadoAlerta.RESUELTA);
			alertaRepository.save(alerta);
			System.out.println("✅ Alerta de " + tipo + " resuelta automáticamente para la zona ID: " + idZona);
		}
	}
}
