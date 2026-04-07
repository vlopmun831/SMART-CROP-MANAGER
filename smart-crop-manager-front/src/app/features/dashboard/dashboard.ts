import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { AuthService } from '../../core/services/auth'; 
import { ZonaService } from '../../core/services/zona/zona';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario/usuario';
@Component({
  selector: 'app-dashboard',
  standalone: true, // Asegúrate de que es standalone
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent implements OnInit {
  // Inyectamos los servicios 
  public authService = inject(AuthService);
  private zonaService = inject(ZonaService);
  private usuarioService = inject(UsuarioService);



  // Usaremos un Signal para guardar las zonas que traigamos del Back
  public zonas = signal<any[]>([]);
  // Signals para el formulario 
  public verFormUsuario = signal<boolean>(false);

  public verFormZona = signal<boolean>(false);
  public usuarios = signal<any[]>([]);

  // Objeto para la nueva zona (siguiendo tu entidad Java)
  public nuevaZona = {
    varCultivo: 'VARIEDAD_1', // Valor por defecto del Enum
    ubicacion: '',
    humSueloMinConfig: 20.0,
    humSueloMaxConfig: 80.0,
    tempMaxConfig: 40.0,
    usuario: { id: null } // Para asignar el dueño
  };

  

  // Objeto para vincular con el formulario
  public nuevoUsuario = {
    nombre: '',
    email: '',
    password: '',
    rol: 'USUARIO' // Por defecto creamos agricultores
  };

  ngOnInit() {
    this.cargarDatos();
  }

 cargarDatos() {
  // Comprobamos el rol usando el signal de tu AuthService
    const rol = this.authService.userRole();
    
    // Si el rol es ROLE_ADMIN (o como lo devuelva tu Java)
    if (rol === 'ADMIN') {
      this.zonaService.getZonas().subscribe({
        next: (data) => this.zonas.set(data),
        error: (err) => console.error('Error Admin:', err)
      });
    } else {
      
      this.zonaService.getZonas().subscribe({
        next: (data) => this.zonas.set(data)
      });
    }
  }

  // Método para el botón del Admin
 crearNuevoUsuario() {
    this.verFormUsuario.set(true); // Muestra el formulario en el HTML
  }

  guardarUsuario() {
    console.log('Enviando datos al servidor...', this.nuevoUsuario);
    
    this.usuarioService.crearUsuario(this.nuevoUsuario).subscribe({
      next: (res) => {
        alert('¡Agricultor registrado correctamente!');
        this.verFormUsuario.set(false); // Oculta el formulario y vuelve a la tabla
        this.limpiarForm();
        this.cargarDatos(); // Refrescamos la lista por si acaso
      },
      error: (err) => {
        console.error('Error al registrar:', err);
        alert('Error al registrar el usuario. Revisa la consola.');
      }
    });
  }

  limpiarForm() {
    this.nuevoUsuario = {
      nombre: '',
      email: '',
      password: '',
      rol: 'USUARIO'
    };
  }

  // 1. Función para abrir el formulario de zonas
  crearNuevaZona() {
    this.verFormZona.set(true);
    this.verFormUsuario.set(false); // Cerramos el otro por si acaso
    
    // Cargamos los usuarios para el desplegable
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => this.usuarios.set(data),
      error: (err) => console.error('Error al traer usuarios:', err)
    });
  }

  // 2. Función para enviar la zona al Backend
  guardarZona() {
    // Si no se eligió usuario, lo enviamos como null (tu Java lo permite)
    if (!this.nuevaZona.usuario.id) {
       this.nuevaZona.usuario = null as any;
    }

    this.zonaService.crearZona(this.nuevaZona).subscribe({
      next: (res) => {
        alert('¡Zona de cultivo configurada con éxito!');
        this.verFormZona.set(false);
        this.cargarDatos(); // Refrescar la tabla
      },
      error: (err) => alert('Error al crear zona: ' + err.error)
    });
  }
}