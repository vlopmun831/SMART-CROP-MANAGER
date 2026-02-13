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

import com.tfg.smart_crop_manager.persistence.entities.Administrador;
import com.tfg.smart_crop_manager.services.AdministradorService;
import com.tfg.smart_crop_manager.services.exceptions.AdministradorException;
import com.tfg.smart_crop_manager.services.exceptions.AdministradorNotFoundException;

@RestController
@RequestMapping("/administrador")

public class AdministradorController {
	
	@Autowired
	private  AdministradorService administradorService;
	
	@GetMapping
	public ResponseEntity<List<Administrador>> list() {
		return ResponseEntity.ok(this.administradorService.findAll());

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable int id) {

		try {

			return ResponseEntity.ok(this.administradorService.findById(id));
		} catch (AdministradorNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Administrador administrador) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(this.administradorService.create(administrador));

		} catch (AdministradorException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}

	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable int id, @RequestBody Administrador administrador) {

		try {
			return ResponseEntity.ok(this.administradorService.update(administrador, id));

		} catch (AdministradorNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable int idAdministrador) {

		try {
			this.administradorService.delete(idAdministrador);
			return ResponseEntity.ok().build();
		} catch (AdministradorNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	

}
