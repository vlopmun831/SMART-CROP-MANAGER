package com.tfg.smart_crop_manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.tfg.smart_crop_manager.services.exceptions.UsuarioException;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioNotFoundException;
import com.tfg.smart_crop_manager_backend.persistence.entities.Usuario;
import com.tfg.smart_crop_manager_backend.persistence.repositories.UsuarioRepository;

@Service

public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public List<Usuario> findAll() {

		return this.usuarioRepository.findAll();
		
	}	
		public Usuario findById(int idUsuario) {

			if (!this.usuarioRepository.existsById(idUsuario)) {
				throw new UsuarioNotFoundException("El id del cliente no existe");
			}

			return this.usuarioRepository.findById(idUsuario).get();
		}

		public Usuario create(Usuario usuario) {


			usuario.setId(0);

			return this.usuarioRepository.save(usuario);

		}

		public Usuario update(Usuario usuario, int idUsuario) {
			if (usuario.getId() != idUsuario) {
				throw new UsuarioException(
						String.format("El id del body %d y el id del path %d  no coinciden", usuario.getId(), idUsuario));
			}
			if (!this.usuarioRepository.existsById(idUsuario)) {

				throw new UsuarioNotFoundException("El id del usuario no existe");
			}


			Usuario usuarioBD = this.findById(idUsuario);
			usuarioBD.setNombre(usuario.getNombre());
			usuarioBD.setEmail(usuario.getEmail());
			usuarioBD.setPassword(usuario.getPassword());

			return this.usuarioRepository.save(usuario);
		}
	
		public void delete(int id) {
			if (!this.usuarioRepository.existsById(id)) {
				throw new UsuarioNotFoundException("El id del cliente no existe");
			}
			this.usuarioRepository.deleteById(id);
		}

}
