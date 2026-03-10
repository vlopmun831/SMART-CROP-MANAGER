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

import com.tfg.smart_crop_manager.dto.ZonaCultivoDTO;
import com.tfg.smart_crop_manager.mappers.ZonaCultivoMapper;
import com.tfg.smart_crop_manager.persistence.entities.ZonaCultivo;
import com.tfg.smart_crop_manager.services.ZonaCultivoService;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoException;
import com.tfg.smart_crop_manager.services.exceptions.ZonaCultivoNotFoundException;

@RestController
@RequestMapping("zonas")
public class ZonaCultivoController {
	
    @Autowired
    private ZonaCultivoService zonaCultivoService;
    
    
 // Listado total para el Administrador/Dueño
    @GetMapping
    public ResponseEntity<List<ZonaCultivoDTO>> findAll() {
        List<ZonaCultivo> zonas = this.zonaCultivoService.findAll();
        return ResponseEntity.ok(ZonaCultivoMapper.toDTOsFuncional(zonas));
    }

    // Listar zonas del usuario - Devuelve lista de DTOs
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarZonasPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<ZonaCultivo> zonas = this.zonaCultivoService.findByUsuario(idUsuario);
            // Usamos el método funcional del mapper para limpiar la salida
            return ResponseEntity.ok(ZonaCultivoMapper.toDTOsFuncional(zonas));
        } catch (ZonaCultivoException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // Obtener detalles de una zona específica - Devuelve DTO
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerZonaPorId(@PathVariable Integer id) {
        try {
            ZonaCultivo zona = this.zonaCultivoService.findById(id);
            return ResponseEntity.ok(ZonaCultivoMapper.toDTO(zona));
        } catch (ZonaCultivoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Crear nuevas zonas - Devuelve DTO de la zona creada
    @PostMapping
    public ResponseEntity<?> crearZona(@RequestBody ZonaCultivo zonaCultivo) {
        try {
            ZonaCultivo nuevaZona = this.zonaCultivoService.create(zonaCultivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(ZonaCultivoMapper.toDTO(nuevaZona));
        } catch (ZonaCultivoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Modificar información - Devuelve DTO actualizado
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarZona(@PathVariable Integer id, @RequestBody ZonaCultivo zonaCultivo) {
        try {
            ZonaCultivo zonaAct = this.zonaCultivoService.update(zonaCultivo, id);
            return ResponseEntity.ok(ZonaCultivoMapper.toDTO(zonaAct));
        } catch (ZonaCultivoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ZonaCultivoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarZona(@PathVariable Integer id) {
        try {
            this.zonaCultivoService.delete(id);
            return ResponseEntity.noContent().build(); // Cambiado a 204 No Content por buena práctica
        } catch (ZonaCultivoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}