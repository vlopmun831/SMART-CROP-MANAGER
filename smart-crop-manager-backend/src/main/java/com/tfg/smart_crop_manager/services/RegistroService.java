package com.tfg.smart_crop_manager.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.Registro;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta;
import com.tfg.smart_crop_manager.persistence.enums.VariedadCultivo;
import com.tfg.smart_crop_manager.persistence.repositories.RegistroRepository;
import com.tfg.smart_crop_manager.services.exceptions.RegistroException;
import com.tfg.smart_crop_manager.services.exceptions.RegistroNotFoundException;

@Service
public class RegistroService {

	@Autowired
	private RegistroRepository registroRepository;

	@Autowired
	private ZonaCultivoService zonaCultivoService; // Para validar la existencia de la zona

	@Autowired
	private AlertaService alertaService;
	
	@Autowired
	private RiegoService riegoService;

	public Registro findById(Integer id) {
		if (id == null || !this.registroRepository.existsById(id)) {
			throw new RegistroNotFoundException("El id del registro no existe.");
		}
		return this.registroRepository.findById(id).get();
	}

	// Obtener solo el registro más reciente de una zona
	public Registro findUltimoPorZona(Integer idZona) {
		// 1. Validamos que la zona existe
		this.zonaCultivoService.findById(idZona);

		// 2. ordena por fecha
		List<Registro> registros = this.registroRepository.findByZonaCultivoIdOrderByFechaDesc(idZona);

		if (registros.isEmpty()) {
			throw new RegistroNotFoundException("Aún no hay datos de sensores para la zona con ID " + idZona);
		}

		// 3. Devolvemos el primero de la lista (el más nuevo gracias al
		// OrderByFechaDesc)
		return registros.get(0);
	}

	// Consultar los datos registrados para cada zona (historial)
	public List<Registro> findByZonaCultivoId(Integer idZona) {

		// Validación de existencia de la zona y no nos de un not found
		zonaCultivoService.findById(idZona);

		return this.registroRepository.findByZonaCultivoIdOrderByFechaDesc(idZona);
	}

	// create
	public Registro create(Registro registro) {

		// Validar que la zona a la que pertenece el registro existe
		if (registro.getZonaCultivo() == null || registro.getZonaCultivo().getId() == null) {

			throw new RegistroException("El registro debe estar asociado a una ZonaCultivo válida.");
		}

		ZonaCultivo zona = zonaCultivoService.findById(registro.getZonaCultivo().getId());
		registro.setZonaCultivo(zona);
		registro.setFecha(LocalDateTime.now(ZoneId.of("Europe/Madrid")));

		Registro registroGuardado = this.registroRepository.save(registro);

		// 4. EJECUTAR MOTOR DE ALERTAS
		// Llamamos a un método privado para no ensuciar el create
		this.comprobarYGenerarAlertas(registroGuardado, zona);

		return registroGuardado;
	}

// (La lógica que "decide" si hay alerta)
	private void comprobarYGenerarAlertas(Registro reg, ZonaCultivo zona) {
		VariedadCultivo variedad = zona.getVarCultivo();

		//  LÓGICA DE UMBRALES 

		// Si el admin puso un mínimo en la zona, usamos ese. Si no (null), usamos el
		// del Enum.
		double minHum = (zona.getHumSueloMinConfig() != null) ? zona.getHumSueloMinConfig() : variedad.getHumSueloMin();

		// Si el admin puso un máximo de temp, usamos ese. Si no, el del Enum.
		double maxTemp = (zona.getTempMaxConfig() != null) ? zona.getTempMaxConfig() : variedad.getTempMax();

		//  COMPROBACIONES 

		// ¿Falta agua?
		if (reg.getHumedadSuelo() < minHum) {
			alertaService.registrarAlertaAutomatica(zona, TipoAlerta.SUELO_SECO,
					"Humedad baja: " + reg.getHumedadSuelo() + "% (Límite: " + minHum + "%)");
		
		//  AUTOMATISMO: Riego sólo
        // Pasamos null en la hora para que el Service use LocalDateTime.now()
        riegoService.iniciarRiego(zona.getId(), LocalDateTime.now(ZoneId.of("Europe/Madrid")));
        
        System.out.println("Riego Aotomático: Humedad baja detectada. Riego activado automáticamente para la zona: " + zona.getUbicacion());
		}

		// ¿Hace demasiado calor?
		if (reg.getTemperatura() > maxTemp) {
			alertaService.registrarAlertaAutomatica(zona, TipoAlerta.CALOR_EXTREMO,
					"Temperatura alta: " + reg.getTemperatura() + "°C (Límite: " + maxTemp + "°C)");
		}

	}

}
