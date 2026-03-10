package com.tfg.smart_crop_manager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.repositories.ZonaCultivoRepository;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioNotFoundException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoNotFoundException;

import org.springframework.transaction.annotation.Transactional;
@Service
public class ZonaCultivoService {
	
	@Autowired
	 private ZonaCultivoRepository zonaCultivoRepository;
	 
	 @Autowired
	 private UsuarioService usuarioService; // Para verificar y obtener el usuario asociado
	 
	 
	 //Todas las zonas
	 public List<ZonaCultivo> findAll() {
		    return this.zonaCultivoRepository.findAll();
		}
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
	    @Transactional(readOnly = true)
		public ZonaCultivo findById(Integer id) { 
			if (id == null || !this.zonaCultivoRepository.existsById(id)) {
				throw new ZonaCultivoNotFoundException(
						String.format("La zona de cultivo con ID %d no existe.", id)
						);
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
			if (zonaCultivo.getUsuario() != null) {
		        zonaBD.setUsuario(zonaCultivo.getUsuario());
		    }
			// 2. NUEVOS CAMPOS DE CONFIGURACIÓN (Admin Control)
		    // Permite que el Admin ajuste los umbrales personalizados
		    if (zonaCultivo.getHumSueloMinConfig() != null) {
		        zonaBD.setHumSueloMinConfig(zonaCultivo.getHumSueloMinConfig());
		    }
		    if (zonaCultivo.getHumSueloMaxConfig() != null) {
		        zonaBD.setHumSueloMaxConfig(zonaCultivo.getHumSueloMaxConfig());
		    }
		    if (zonaCultivo.getTempMaxConfig() != null) {
		        zonaBD.setTempMaxConfig(zonaCultivo.getTempMaxConfig());
		    }
			
			return this.zonaCultivoRepository.save(zonaBD);
		}
		
		public void delete(int id) {
			if (!this.zonaCultivoRepository.existsById(id)) {
				throw new ZonaCultivoNotFoundException(
						String.format("La zona de cultivo con ID %d no existe.", id)
						);
			}
			this.zonaCultivoRepository.deleteById(id);
		}
		
}
