package com.tfg.smart_crop_manager.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.Riego;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.repositories.RiegoRepository;
import com.tfg.smart_crop_manager.services.exceptions.RiegoException;
import com.tfg.smart_crop_manager.services.exceptions.RiegoNotFoundException;

@Service
public class RiegoService {
	
	@Autowired
	private RiegoRepository riegoRepository;
	
	@Autowired
	private ZonaCultivoService zonaCultivoService;
    

    // Consultar el historial de riego para una zona
    public List<Riego> findByZonaCultivoId(int idZona) {
        zonaCultivoService.findById(idZona); 
        return this.riegoRepository.findByZonaCultivoIdOrderByFechaDescHoraInicioDesc(idZona);
    }
    
	public Riego findById(Integer id) {
		if (id == null || !this.riegoRepository.existsById(id)) {
			throw new RiegoNotFoundException("El id del registro de riego no existe.");
		}
		return this.riegoRepository.findById(id).get();
	}

   
    
    // Controlar el riego (Iniciar riego y registrar)
	public Riego iniciarRiego(Integer idZona, LocalDate horaInicio) {
        // En un proyecto real, aquí iría la llamada a la API del hardware
        
        ZonaCultivo zona = zonaCultivoService.findById(idZona);
        
        Riego nuevoRiego = new Riego();
        nuevoRiego.setFecha(LocalDate.now());
        nuevoRiego.setHoraInicio(horaInicio != null ? horaInicio : LocalDate.now());
        // La horaFin se podría establecer más tarde al detener el riego
        nuevoRiego.setZonaCultivo(zona);
        
		return this.riegoRepository.save(nuevoRiego);
	}
    
    // Finalizar/Detener el riego (actualizando el registro existente)
    public Riego finalizarRiego(Integer idRiego, LocalDate horaFin) {
        Riego riegoBD = this.findById(idRiego);
        
        if (riegoBD.getHoraFin() != null) {
            throw new RiegoException("El registro de riego ya tiene una hora de finalización.");
        }
        
        riegoBD.setHoraFin(horaFin != null ? horaFin : LocalDate.now());
        
        return this.riegoRepository.save(riegoBD);
    }
    
    public void delete(Integer id) {
		if (!this.riegoRepository.existsById(id)) {
			throw new RiegoNotFoundException("El id del registro de riego no existe.");
		}
		this.riegoRepository.deleteById(id);
	}
	

}
