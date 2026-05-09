package com.tfg.smart_crop_manager.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.dto.UsuarioDTO;
import com.tfg.smart_crop_manager.mappers.UsuarioMapper;
import com.tfg.smart_crop_manager.persistence.entities.Usuario;
import com.tfg.smart_crop_manager.services.UsuarioService;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioException;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioNotFoundException;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> findAll() {
		List<Usuario> usuarios = this.usuarioService.findAll();
		return ResponseEntity.ok(UsuarioMapper.toDTOsFuncional(usuarios));

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Integer id) {

		try {
			Usuario usuario = this.usuarioService.findById(id);
			return ResponseEntity.ok(UsuarioMapper.toDTO(usuario));
		} catch (UsuarioNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Usuario usuario) {
		try {
			Usuario nuevoUsuario = this.usuarioService.create(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDTO(nuevoUsuario));

		} catch (UsuarioException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}

	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Usuario usuario) {

		try {
			Usuario usuarioAct = this.usuarioService.update(usuario, id);
			return ResponseEntity.ok(UsuarioMapper.toDTO(usuarioAct));

		} catch (UsuarioNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {

		try {
			this.usuarioService.deleteUsuario(id);
			return ResponseEntity.ok().build();
		} catch (UsuarioNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

}
