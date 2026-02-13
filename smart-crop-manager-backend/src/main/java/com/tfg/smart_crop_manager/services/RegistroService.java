package com.tfg.smart_crop_manager.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.Registro;
import com.tfg.smart_crop_manager.persistence.repositories.RegistroRepository;
import com.tfg.smart_crop_manager.services.exceptions.RegistroException;
import com.tfg.smart_crop_manager.services.exceptions.RegistroNotFoundException;

@Service
public class RegistroService {

	@Autowired
	private RegistroRepository registroRepository;

	@Autowired
	private ZonaCultivoService zonaCultivoService; // Para validar la existencia de la zona

	public Registro findById(Integer id) { 
		if (id == null || !this.registroRepository.existsById(id)) {
			throw new RegistroNotFoundException("El id del registro no existe.");
		}
		return this.registroRepository.findById(id).get();
	}
		
	// Consultar los datos registrados para cada zona (historial)
	public List<Registro> findByZonaCultivoId(Integer idZona) { 

		//  Validación de existencia de la zona y no nos de un not found
		zonaCultivoService.findById(idZona);

	
		return this.registroRepository.findByZonaCultivoIdOrderByFechaDesc(idZona);
	}

	//create
	public Registro create(Registro registro) {

		// Validar que la zona a la que pertenece el registro existe
		if (registro.getZonaCultivo() == null || registro.getZonaCultivo().getId() == null) {
		
			throw new RegistroException("El registro debe estar asociado a una ZonaCultivo válida.");
		}

		// Verifica existencia de la ZonaCultivo. Lanza ZonaCultivoNotFoundException si
		// no existe.
		zonaCultivoService.findById(registro.getZonaCultivo().getId());

		registro.setFecha(LocalDateTime.now());

		return this.registroRepository.save(registro);
	}



	//delete
	public void deleteById(Integer id) { 
		if(!this.registroRepository.existsById(id)) {
			throw new RegistroNotFoundException("El ID indicado no existe. ");
		}
		
		this.registroRepository.deleteById(id);
	}

	
	
}
