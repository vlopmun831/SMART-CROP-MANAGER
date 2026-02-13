package com.tfg.smart_crop_manager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.smart_crop_manager.persistence.entities.Usuario;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.persistence.repositories.UsuarioRepository;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioException;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioNotFoundException;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public List<Usuario> findAll() {

		return this.usuarioRepository.findAll();

	}

	public Usuario findById(Integer id) {

		if (!this.usuarioRepository.existsById(id)) {
			throw new UsuarioNotFoundException("El id del usuario no existe");
		}

		return this.usuarioRepository.findById(id).get();
	}

	public Usuario create(Usuario usuario) {
		if(usuario.getZonasCultivo() != null) {
			for (ZonaCultivo zona : usuario.getZonasCultivo()) {
				zona.setUsuario(usuario);
			}
		}

		return this.usuarioRepository.save(usuario);

	}

	public Usuario update(Usuario usuario, Integer id) {

		if (usuario.getId() == null || !usuario.getId().equals(id)) {
			throw new UsuarioException(
					String.format("El id del body %d y el id del path %d  no coinciden", usuario.getId(), id));
		}
		Usuario usuarioBD = this.findById(id);
		usuarioBD.setNombre(usuario.getNombre());
		usuarioBD.setEmail(usuario.getEmail());
		usuarioBD.setPassword(usuario.getPassword());

		return this.usuarioRepository.save(usuarioBD);
	}

	public void delete(Integer id) {
		if (!this.usuarioRepository.existsById(id)) {
			throw new UsuarioNotFoundException("El id del administrador no existe");
		}
		this.usuarioRepository.deleteById(id);
	}

}
