package com.tfg.smart_crop_manager.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.smart_crop_manager.persistence.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	
	List<Usuario> findByEmail(String email);

}
