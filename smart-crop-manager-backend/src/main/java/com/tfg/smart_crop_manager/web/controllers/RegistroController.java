package com.tfg.smart_crop_manager.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.dto.SensorPayloadDTO;
import com.tfg.smart_crop_manager.mappers.RegistroMapper;
import com.tfg.smart_crop_manager.persistence.entities.Registro;
import com.tfg.smart_crop_manager.services.RegistroService;
import com.tfg.smart_crop_manager.services.exceptions.RegistroNotFoundException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoNotFoundException;

@RestController
@RequestMapping("registros")
public class RegistroController {

	@Autowired
	private RegistroService registroService;

	@GetMapping("/zona/{idZona}/ultimo")
	public ResponseEntity<?> obtenerUltimoRegistro(@PathVariable Integer idZona) {
		try {
			Registro ultimo = this.registroService.findUltimoPorZona(idZona);
			return ResponseEntity.ok(RegistroMapper.toDTO(ultimo));
		} catch (ZonaCultivoNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (RegistroNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Consultar los datos registrados para cada zona (Historial)
	@GetMapping("/zona/{idZona}")
	public ResponseEntity<?> listarRegistrosPorZona(@PathVariable Integer idZona) {
		try {
			List<Registro> registros = this.registroService.findByZonaCultivoId(idZona);
			return ResponseEntity.ok(RegistroMapper.toDTOsFuncional(registros));
		} catch (ZonaCultivoNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Simulación: Recepción de datos de un sensor
	@PostMapping
	public ResponseEntity<?> recibirDatosSensor(@RequestBody Registro registro) {
		try {
			Registro nuevoRegistro = this.registroService.create(registro);
			return ResponseEntity.status(HttpStatus.CREATED).body(RegistroMapper.toDTO(nuevoRegistro));
		} catch (RegistroNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (ZonaCultivoNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("La Zona de Cultivo especificada para el registro no existe.");
		}
	}

	// En caso real de conexion con sensores
	@PostMapping("/iot/recepcion")
	public ResponseEntity<?> recibirDatosDispositivoIoT(@RequestBody SensorPayloadDTO payload) {
		try {
			// 1. Simulación de seguridad para el futuro
			if (payload.getApiKey() == null || !payload.getApiKey().equals("TFG_SECRET_KEY_2024")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API Key inválida o no proporcionada");
			}

			// 2. Aquí el servicio se encargará de buscar la Zona por ID,
			// poner la fecha automática (LocalDateTime.now()) y guardar el Registro.
			Registro guardado = this.registroService.procesarLecturaSensor(payload);

			// 3. Los sensores reales solo necesitan un "OK", no el DTO entero de vuelta
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("{\"status\": \"ok\", \"mensaje\": \"Lectura procesada\"}");

		} catch (ZonaCultivoNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando lectura del sensor");
		}
	}
}
