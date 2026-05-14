package com.tfg.smart_crop_manager.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.dto.SensorPayloadDTO;
import com.tfg.smart_crop_manager.persistence.entities.Registro;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.enums.TipoAlerta;
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
		this.zonaCultivoService.findById(idZona);

		List<Registro> registros = this.registroRepository.findByZonaCultivoIdOrderByFechaDesc(idZona);

		if (registros.isEmpty()) {
			throw new RegistroNotFoundException("Aún no hay datos de sensores para la zona con ID " + idZona);
		}

		return registros.get(0);
	}

	// Consultar los datos registrados para cada zona (historial)
	public List<Registro> findByZonaCultivoId(Integer idZona) {

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
		// Llamamos a un método privado para no ensuciar el create
		this.comprobarYGenerarAlertas(registroGuardado, zona);

		return registroGuardado;
	}

	// Aqui procesariamos su lectura (Desde la placa física)
	public Registro procesarLecturaSensor(SensorPayloadDTO payload) {
		// 1. Buscamos la zona a la que pertenece el sensor
		ZonaCultivo zona = zonaCultivoService.findById(payload.getIdZona());

		// 2. Creamos el registro con los datos que nos manda la placa
		Registro nuevoRegistro = new Registro();

		// Le ponemos la hora exacta con la zona de Madrid (igual que en el create)
		nuevoRegistro.setFecha(LocalDateTime.now(ZoneId.of("Europe/Madrid")));
		nuevoRegistro.setTemperatura(payload.getTemperatura());
		nuevoRegistro.setHumedadSuelo(payload.getHumedadSuelo());
		nuevoRegistro.setLluvia(payload.isLluvia());
		nuevoRegistro.setZonaCultivo(zona);

		// Guardamos en la base de datos
		Registro registroGuardado = this.registroRepository.save(nuevoRegistro);

		this.comprobarYGenerarAlertas(registroGuardado, zona);

		return registroGuardado;
	}

	// (La lógica que "decide" si hay alerta)
	private void comprobarYGenerarAlertas(Registro reg, ZonaCultivo zona) {

		// --- 1. LÓGICA DE UMBRALES ---

		double minHum = (zona.getHumSueloMinConfig() != null) ? zona.getHumSueloMinConfig() : 30.0;
		double maxHum = (zona.getHumSueloMaxConfig() != null) ? zona.getHumSueloMaxConfig() : 80.0;
		double maxTemp = (zona.getTempMaxConfig() != null) ? zona.getTempMaxConfig() : 35.0;

		// --- 2. COMPROBACIONES DE ALERTAS Y ENCENDIDO ---

		// ¿Falta agua y NO está lloviendo? -> ENCENDEMOS
		if (reg.getHumedadSuelo() < minHum && !reg.isLluvia()) {
			alertaService.registrarAlertaAutomatica(zona, TipoAlerta.SUELO_SECO,
					"Humedad baja: " + reg.getHumedadSuelo() + "% (Límite: " + minHum + "%)");

			try {
				riegoService.iniciarRiego(zona.getId(), LocalDateTime.now(ZoneId.of("Europe/Madrid")));
				System.out.println("🌱 Riego Automático INICIADO: Humedad baja en " + zona.getUbicacion());
			} catch (Exception e) {
				// Silencioso: Ya estaba encendido, no hacemos nada.
			}
		}

		// --- 3. APAGADO AUTOMÁTICO ---

		// ¿La tierra ya está bien mojada O ha empezado a llover? -> APAGAMOS
		if (reg.getHumedadSuelo() >= maxHum || reg.isLluvia()) {
			try {
				// Llamamos al método que creamos antes para apagar el riego de esta parcela
				riegoService.finalizarRiegoActivoPorZona(zona.getId());
				alertaService.resolverAlertaPorZonaYTipo(zona.getId().intValue(), TipoAlerta.SUELO_SECO);

				System.out.println("🛑 Riego Automático DETENIDO: Humedad óptima o lluvia en " + zona.getUbicacion());
			} catch (Exception e) {
				// Silencioso: Entrará aquí la mayoría de las veces porque el riego ya estará
				// apagado
				
			}
		}

		// --- 4. COMPROBACIÓN DE TEMPERATURA ---

		// ¿Hace demasiado calor?
		if (reg.getTemperatura() > maxTemp) {
			alertaService.registrarAlertaAutomatica(zona, TipoAlerta.CALOR_EXTREMO,
					"Temperatura alta: " + reg.getTemperatura() + "°C (Límite: " + maxTemp + "°C)");
		}
	}
}
