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

	// Aqui procesariamos su lectura
	public Registro procesarLecturaSensor(SensorPayloadDTO payload) {
		// 1. Buscamos la zona a la que pertenece el sensor
		ZonaCultivo zona = zonaCultivoService.findById(payload.getIdZona());

		// 2. Creamos el registro con los datos que nos manda la placa
		Registro nuevoRegistro = new Registro();
		// El sensor no sabe qué hora es, así que el servidor le pone la hora exacta del
		// sistema:
		nuevoRegistro.setFecha(LocalDateTime.now());
		nuevoRegistro.setTemperatura(payload.getTemperatura());
		nuevoRegistro.setHumedadSuelo(payload.getHumedadSuelo());
		nuevoRegistro.setHumedadAire(payload.getHumedadAire());
		nuevoRegistro.setLluvia(payload.isLluvia());

		nuevoRegistro.setZonaCultivo(zona);

		return this.registroRepository.save(nuevoRegistro);
	}

	// (La lógica que "decide" si hay alerta)
	private void comprobarYGenerarAlertas(Registro reg, ZonaCultivo zona) {
		VariedadCultivo variedad = zona.getVarCultivo();

		// --- 1. LÓGICA DE UMBRALES ---
		
		double minHum = (zona.getHumSueloMinConfig() != null) ? zona.getHumSueloMinConfig() : variedad.getHumSueloMin();
		// Asumimos que tienes el getter del máximo también en tu Enum de variedades
		double maxHum = (zona.getHumSueloMaxConfig() != null) ? zona.getHumSueloMaxConfig() : variedad.getHumSueloMax();
		double maxTemp = (zona.getTempMaxConfig() != null) ? zona.getTempMaxConfig() : variedad.getTempMax();

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

		// --- 3. LA MAGIA NUEVA: APAGADO AUTOMÁTICO ---
		
		// ¿La tierra ya está bien mojada O ha empezado a llover? -> APAGAMOS
		if (reg.getHumedadSuelo() >= maxHum || reg.isLluvia()) {
            try {
                // Llamamos al método que creamos antes para apagar el riego de esta parcela
                riegoService.finalizarRiegoActivoPorZona(zona.getId());
                System.out.println("🛑 Riego Automático DETENIDO: Humedad óptima o lluvia en " + zona.getUbicacion());
            } catch (Exception e) {
                // Silencioso: Entrará aquí la mayoría de las veces porque el riego ya estará apagado,
                // así evitamos que salten errores en la consola.
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
