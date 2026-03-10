package com.tfg.smart_crop_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.smart_crop_manager.dto.AlertaDTO;
import com.tfg.smart_crop_manager.services.AlertaService;
import com.tfg.smart_crop_manager.services.exceptions.AlertaException;
import com.tfg.smart_crop_manager.services.exceptions.AlertaNotFoundException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoNotFoundException;

@RestController
@RequestMapping("alertas")
public class AlertaController {
	
	@Autowired
    private AlertaService alertaService;
	
	
	// Obtener alertas sin resolver de todas las zonas de un usuario
	@GetMapping("/usuario/{idUsuario}/pendientes")
	public ResponseEntity<?> listarAlertasPendientesUsuario(@PathVariable Integer idUsuario) {
	    try {
	        // El service debería filtrar por resuelta = false
	        List<AlertaDTO> pendientes = this.alertaService.findPendientesByUsuario(idUsuario);
	        // Recuerda usar un AlertaMapper si quieres devolver DTOs
	        return ResponseEntity.ok(pendientes); 
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}
  
    
    // Ver todas las alertas programadas / activas de una zona
    @GetMapping("/zona/{idZona}")
    public ResponseEntity<?> listarAlertasPorZona(@PathVariable Integer idZona) {
        try {
            return ResponseEntity.ok(this.alertaService.findByZonaCultivoId(idZona));
        } catch (ZonaCultivoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    //  Marcar alertas como resueltas
    @PutMapping("/{id}/resolver")
    public ResponseEntity<?> resolverAlerta(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(this.alertaService.marcarComoResuelta(id));
        } catch (AlertaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AlertaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
   
    

}
