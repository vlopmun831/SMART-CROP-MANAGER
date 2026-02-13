package com.tfg.smart_crop_manager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.Administrador;
import com.tfg.smart_crop_manager.persistence.repositories.AdministradorRepository;
import com.tfg.smart_crop_manager.services.exceptions.AdministradorException;
import com.tfg.smart_crop_manager.services.exceptions.AdministradorNotFoundException;

@Service
public class AdministradorService {
    @Autowired
	private AdministradorRepository administradorRepository;

    public List<Administrador> findAll() {

		return this.administradorRepository.findAll();
		
	}	
		public Administrador findById(int id) {

			if (!this.administradorRepository.existsById(id)) {
				throw new AdministradorNotFoundException("El id del cliente no existe");
			}

			return this.administradorRepository.findById(id).get();
		}

		public Administrador create(Administrador cliente) {


			cliente.setId(0);

			return this.administradorRepository.save(cliente);

		}

		public Administrador update(Administrador administrador, int id) {

				if (administrador.getId() != id) {
					throw new AdministradorException(
							String.format("El id del body %d y el id del path %d  no coinciden", administrador.getId(), id));
				}
			Administrador administradorBD = this.findById(id);
			administradorBD.setId(administrador.getId());
			administradorBD.setEmail(administrador.getEmail());
			administradorBD.setPassword(administrador.getPassword());

			return this.administradorRepository.save(administradorBD);
			}
		

		public void delete(int id) {
			if (!this.administradorRepository.existsById(id)) {
				throw new AdministradorNotFoundException("El id del administrador no existe");
			}
			this.administradorRepository.deleteById(id);
		}

    
   
    
}
    

