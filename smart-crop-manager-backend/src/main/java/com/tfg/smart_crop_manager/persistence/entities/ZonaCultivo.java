package com.tfg.smart_crop_manager.persistence.entities;

import java.util.ArrayList;
import java.util.List;

import com.tfg.smart_crop_manager.persistence.enums.VariedadCultivo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "zona_cultivo")
@Getter
@Setter
@NoArgsConstructor
public class ZonaCultivo {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "var_cultivo") // Nombre de columna en la BD
	    private VariedadCultivo varCultivo;

	    private String ubicacion;
	 // Relación N:1 con Usuario
	    @ManyToOne
	    @JoinColumn(name = "id_usuario", nullable = false)
	    private Usuario usuario;
	    
	    @OneToMany(mappedBy = "zonaCultivo", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Registro> registros = new ArrayList<>();

	    
}
