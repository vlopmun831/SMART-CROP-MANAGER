package com.tfg.smart_crop_manager.controllers;

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

import com.tfg.smart_crop_manager.services.UsuarioService;
import com.tfg.smart_crop_manager.services.exceptions.UsuarioNotFoundException;
import com.tfg.smart_crop_manager_backend.persistence.entities.Usuario;

@RestController
@RequestMapping("/usuario")

public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity<List<Usuario>> list() {
		return ResponseEntity.ok(this.usuarioService.findAll());

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable int id) {

		try {

			return ResponseEntity.ok(this.usuarioService.findById(id));
		} catch (UsuarioNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}
    @PostMapping
    public Usuario createCliente(@RequestBody Usuario usuario) {
        return usuarioService.create(usuario);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(updateUsuario(id, usuario));
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable int id){
		try {
			this.usuarioService.delete(id);
			return ResponseEntity.ok().build();
		}
		catch(UsuarioNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

}
