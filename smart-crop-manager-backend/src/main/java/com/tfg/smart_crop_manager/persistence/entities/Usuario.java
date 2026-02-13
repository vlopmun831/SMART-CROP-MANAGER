package com.tfg.smart_crop_manager.persistence.entities;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	  	
	    private String nombre;

	    @Column(nullable = false, unique = true,length = 100 )
	    private String email;

	    @Column(nullable = false)
	    private String password;

	   
	    
	  


	 // Relación 1:N con ZonaCultivo (el usuario gestiona varias zonas)
	    // El atributo 'usuario' en ZonaCultivo es el campo de mapeo (mappedBy)
	    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	    
	    private List<ZonaCultivo> zonasCultivo = new ArrayList<>();//Con esto evito que me de un NULL POinter Exception
	}

  