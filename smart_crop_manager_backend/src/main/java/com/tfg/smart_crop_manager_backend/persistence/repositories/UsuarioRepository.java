package com.tfg.smart_crop_manager_backend.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.smart_crop_manager_backend.persistence.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

}
