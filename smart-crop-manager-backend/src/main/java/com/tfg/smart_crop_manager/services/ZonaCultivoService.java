package com.tfg.smart_crop_manager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.repositories.ZonaCultivoRepository;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioNotFoundException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoNotFoundException;

@Service
public class ZonaCultivoService {
	
	@Autowired
	 private ZonaCultivoRepository zonaCultivoRepository;
	 
	 @Autowired
	 private UsuarioService usuarioService; // Para verificar y obtener el usuario asociado
	 
	//  Consultar zonas del usuario 
	    public List<ZonaCultivo> findByUsuario(Integer idUsuario) { // USAR INTEGER
	        try {
	             // Validar la existencia del usuario usando el servicio de Usuario
	             usuarioService.findById(idUsuario); 
	        } catch (UsuarioNotFoundException e) {
	             throw new ZonaCultivoException("El usuario asociado no existe."); 
	        }
	        return this.zonaCultivoRepository.findByUsuarioId(idUsuario);
	    }
	   //Consultar una zona de cultivo
		public ZonaCultivo findById(Integer id) { 
			if (id == null || !this.zonaCultivoRepository.existsById(id)) {
				throw new ZonaCultivoNotFoundException("El id de la zona de cultivo no existe.");
			}
			return this.zonaCultivoRepository.findById(id).get();
		}

	
	// Crear nueva zona de cultivo
		public ZonaCultivo create(ZonaCultivo zonaCultivo) {
	        
			
	        if (zonaCultivo.getVarCultivo() == null) {
	            throw new ZonaCultivoException("Debe especificar la variedad de cultivo (varCultivo).");
	        }
			
			return this.zonaCultivoRepository.save(zonaCultivo);
		}

		// Modificar información de una zona
		public ZonaCultivo update(ZonaCultivo zonaCultivo, int id) {
			
			if (zonaCultivo.getId() != 0L && zonaCultivo.getId() != id) {
				throw new ZonaCultivoException(
						String.format("El id del body %d y el id del path %d no coinciden", zonaCultivo.getId(), id));
			}
			
			ZonaCultivo zonaBD = this.findById(id); 
			
			// Actualizar datos, incluyendo la variedad de cultivo
			if (zonaCultivo.getVarCultivo() != null) {
	            zonaBD.setVarCultivo(zonaCultivo.getVarCultivo());
	        }
			if (zonaCultivo.getUbicacion() != null) {
	            zonaBD.setUbicacion(zonaCultivo.getUbicacion());
	        }
			
			return this.zonaCultivoRepository.save(zonaBD);
		}
		
		public void delete(int id) {
			if (!this.zonaCultivoRepository.existsById(id)) {
				throw new ZonaCultivoNotFoundException("El id de la alerta no existe.");
			}
			this.zonaCultivoRepository.deleteById(id);
		}
		
}
