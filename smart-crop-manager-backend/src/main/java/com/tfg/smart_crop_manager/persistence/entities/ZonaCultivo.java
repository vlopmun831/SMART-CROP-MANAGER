package com.tfg.smart_crop_manager.persistence.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

	private String varCultivo;

	private String ubicacion;

	private Double humSueloMinConfig; // Para alertas de SUELO_SECO
	private Double humSueloMaxConfig; // Para alertas de SUELO_ENCHARCADO
	private Double tempMaxConfig; // Para alertas de CALOR_EXTREMO

	// Relación N:1 con Usuario
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario", nullable = true) // El 'true' permite zonas sin dueño
	private Usuario usuario;

	@OneToMany(mappedBy = "zonaCultivo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Registro> registros = new ArrayList<>();

}
