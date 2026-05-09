package com.tfg.smart_crop_manager.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.dto.RiegoDTO;
import com.tfg.smart_crop_manager.mappers.RiegoMapper;
import com.tfg.smart_crop_manager.persistence.entities.Riego;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta; // Importante añadir
import com.tfg.smart_crop_manager.persistence.repositories.RiegoRepository;
import com.tfg.smart_crop_manager.services.exceptions.RiegoException;
import com.tfg.smart_crop_manager.services.exceptions.RiegoNotFoundException;

@Service
public class RiegoService {

	@Autowired
	private RiegoRepository riegoRepository;

	@Autowired
	private ZonaCultivoService zonaCultivoService;

	@Autowired
	private AlertaService alertaService; // Inyectado correctamente

	// Consultar el historial de riego para una zona
	public List<RiegoDTO> findByZonaCultivoId(int idZona) {
		zonaCultivoService.findById(idZona);
		List<Riego> riegos = this.riegoRepository.findByZonaCultivoIdOrderByFechaDescHoraInicioDesc(idZona);
		return riegos.stream().map(RiegoMapper::toDTO).collect(Collectors.toList());
	}

	public Riego findById(Integer id) {
		if (id == null || !this.riegoRepository.existsById(id)) {
			throw new RiegoNotFoundException("El id del registro de riego no existe.");
		}
		return this.riegoRepository.findById(id).get();
	}

	// Iniciar riego y registrar
	public Riego iniciarRiego(Integer idZona, LocalDateTime horaInicio) {
		ZonaCultivo zona = zonaCultivoService.findById(idZona);

		Riego nuevoRiego = new Riego();
		nuevoRiego.setFecha(LocalDate.now());
		nuevoRiego.setHoraInicio(horaInicio != null ? horaInicio : LocalDateTime.now());
		nuevoRiego.setZonaCultivo(zona);

		return this.riegoRepository.save(nuevoRiego);
	}

	public Riego finalizarRiegoActivoPorZona(Integer idZona) {
		// 1. Buscamos el riego abierto de esta zona
		List<Riego> riegosAbiertos = this.riegoRepository.findByZonaCultivoIdAndHoraFinIsNull(idZona);

		if (riegosAbiertos.isEmpty()) {
			throw new RiegoNotFoundException("No hay ningún riego activo en esta zona para detener.");
		}

		// 2. Lo cerramos
		LocalDateTime ahora = LocalDateTime.now();
		Riego ultimoCerrado = null;

		for (Riego riego : riegosAbiertos) {
			riego.setHoraFin(ahora);
			ultimoCerrado = this.riegoRepository.save(riego);
		}

		// 3. ⚡ RESOLVER ALERTA AUTOMÁTICAMENTE ⚡
		// Llamamos al método que creamos en AlertaService para que el Front se
		// actualice
		alertaService.resolverAlertaPorZonaYTipo(idZona, TipoAlerta.SUELO_SECO);

		return ultimoCerrado;
	}

	// Finalizar/Detener el riego manual (por ID)
	public Riego finalizarRiego(Integer idRiego, LocalDateTime horaFin) {
		Riego riegoBD = this.findById(idRiego);

		if (riegoBD.getHoraFin() != null) {
			throw new RiegoException("El registro de riego ya tiene una hora de finalización.");
		}

		riegoBD.setHoraFin(horaFin != null ? horaFin : LocalDateTime.now());

		return this.riegoRepository.save(riegoBD);
	}

	public void delete(Integer id) {
		if (!this.riegoRepository.existsById(id)) {
			throw new RiegoNotFoundException("El id del registro de riego no existe.");
		}
		this.riegoRepository.deleteById(id);
	}
}