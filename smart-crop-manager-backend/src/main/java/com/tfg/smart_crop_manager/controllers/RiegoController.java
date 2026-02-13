package com.tfg.smart_crop_manager.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.services.RiegoService;
import com.tfg.smart_crop_manager.services.exceptions.RiegoException;
import com.tfg.smart_crop_manager.services.exceptions.RiegoNotFoundException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoNotFoundException;

@RestController
@RequestMapping("riego")
public class RiegoController {
	
	@Autowired
    private RiegoService riegoService;

    // Requisito: Controlar el riego de cada zona (Iniciar riego)
    @PostMapping("/zona/{idZona}/iniciar")
    public ResponseEntity<?> iniciarRiego(
        @PathVariable Integer idZona,
        @RequestParam(required = false) LocalDate horaInicio) { 
        
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.riegoService.iniciarRiego(idZona, horaInicio));
        } catch (ZonaCultivoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // Controlar el riego (Finalizar riego)
    @PutMapping("/{idRiego}/finalizar")
    public ResponseEntity<?> finalizarRiego(
        @PathVariable Integer idRiego,
        @RequestParam(required = false) LocalDate horaFin) {
        
        try {
            return ResponseEntity.ok(this.riegoService.finalizarRiego(idRiego, horaFin));
        } catch (RiegoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RiegoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Consultar el historial de riego de una zona
    @GetMapping("/zona/{idZona}/historial")
    public ResponseEntity<?> obtenerHistorialRiego(@PathVariable Integer idZona) {
        try {
            return ResponseEntity.ok(this.riegoService.findByZonaCultivoId(idZona));
        } catch (ZonaCultivoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // Eliminar un registro de riego
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRiego(@PathVariable Integer id) {
        try {
            this.riegoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RiegoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
