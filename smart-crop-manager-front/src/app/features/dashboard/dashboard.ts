import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { AuthService } from '../../core/services/auth'; 
import { ZonaService } from '../../core/services/zona/zona';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario/usuario';
import { AlertaService } from '../../core/services/alerta/alerta';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent implements OnInit {
  // 1. Inyectamos los servicios
  public authService = inject(AuthService);
  private zonaService = inject(ZonaService);
  private usuarioService = inject(UsuarioService);
  private alertaService = inject(AlertaService);

  // 2. Signals para los datos
  public zonas = signal<any[]>([]);
  public usuarios = signal<any[]>([]);

  busquedaOperario = signal(''); 

  // 2. EL COMPUTED - Justo debajo de las señales
  // Importante: Va fuera del constructor y fuera de cualquier función
  operariosFiltrados = computed(() => {
    const query = this.busquedaOperario().toLowerCase();
    return this.usuarios().filter(u => 
      u.rol !== 'ADMIN' && 
      (u.nombre.toLowerCase().includes(query) || u.email.toLowerCase().includes(query))
    );
  });
  public alertasPendientes = signal<any[]>([]);

  public totalZonas = signal<number>(0);
  public totalAlertasGlobales = signal<number>(0);
  public totalUsuarios = signal<number>(0);
    
  // 3. Signals para controlar qué formulario se ve
  public verFormUsuario = signal<boolean>(false);
  public verFormZona = signal<boolean>(false);

   public verListaUsuarios = signal(false);

  // 4. Objetos para los formularios (Vincular con ngModel)
  public nuevaZona = {
    varCultivo: 'VARIEDAD_1',
    ubicacion: '',
    humSueloMinConfig: '',
    humSueloMaxConfig: '',
    tempMaxConfig: '',
    usuario: { id: null as any }
  };

  public nuevoUsuario = {
    nombre: '',
    email: '',
    password: '',
    rol: 'USUARIO'
  };

  ngOnInit() {
    this.cargarDatosSegunRol();
  }

  // Lógica principal de carga
  cargarDatosSegunRol() {
    const rol = this.authService.userRole();
    const userId = this.authService.userId();

    if (rol === 'ADMIN') {
    this.zonaService.getZonas().subscribe(data => {
    this.zonas.set(data);
    this.totalZonas.set(data.length); //  cuántas zonas hay
  });

  this.usuarioService.getUsuarios().subscribe(data => {
    this.usuarios.set(data);
    this.totalUsuarios.set(data.length); // cuántos usuarios hay
  });
  this.alertaService.getAlertasGlobales().subscribe(data => {
   this.alertasPendientes.set(data);
  
  // ⚡ Calculamos solo las que están en rojo para el contador
  const soloRojas = data.filter(a => a.estado === 'PENDIENTE').length;
  this.totalAlertasGlobales.set(soloRojas); // Este es el que usaremos en el KPI
});
  this.usuarioService.getUsuarios().subscribe({
    next: (data) => {
      // Filtramos para que solo lleguen los agricultores y no el Admin si quieres
      this.usuarios.set(data); 
      console.log('Operarios cargados:', data); // Mira la consola para estar segura
    },
    error: (err) => console.error('Error al cargar operarios', err)
  });

}
    else if (rol === 'USUARIO' && userId) {
      this.zonaService.getZonasPorUsuario(userId).subscribe(data => this.zonas.set(data));
      this.alertaService.getAlertasPendientes(userId).subscribe(data => this.alertasPendientes.set(data));
    }
  }

  // --- GESTIÓN DE ALERTAS ---

  seleccionarOperario(usuario: any) {
    this.nuevaZona.usuario.id = usuario.id;
    this.busquedaOperario.set(usuario.nombre); 

  }
 resolverAlerta(idAlerta: number) {
  // 1. Opcional: Podrías poner un confirm aquí también, pero suele ser mejor acción directa
  this.alertaService.cambiarEstado(idAlerta, 'RESUELTA').subscribe({
    next: () => {
      // 2. Avisamos al usuario con estilo
      Swal.fire({
        title: '¡Resuelta!',
        text: 'La incidencia ha sido marcada como finalizada.',
        icon: 'success',
        timer: 1500, // Se cierra solo en segundo y medio
        showConfirmButton: false,
        background: '#0f172a',
        color: '#ffffff'
      });
      
      // 3. ¡IMPORTANTE! Recargamos los datos para que desaparezca de la lista
      this.cargarDatosSegunRol();
    },
    error: (err) => {
      Swal.fire({
        title: 'Error',
        text: 'No se pudo actualizar el estado de la alerta.',
        icon: 'error',
        background: '#0f172a',
        color: '#ffffff'
      });
    }
  });
}

  // --- GESTIÓN DE USUARIOS (ADMIN) ---
  crearNuevoUsuario() {
    this.verFormUsuario.set(true);
    this.verFormZona.set(false);
  }

  guardarUsuario() {
  this.usuarioService.crearUsuario(this.nuevoUsuario).subscribe({
    next: (resp) => {
      // ⚡ SweetAlert de éxito
      Swal.fire({
        title: '¡Registro Exitoso!',
        text: `El agricultor ${this.nuevoUsuario.nombre} ha sido dado de alta.`,
        icon: 'success',
        background: '#0f172a',
        color: '#ffffff',
        confirmButtonColor: '#10b981',
        timer: 2000, // Se cierra solo a los 2 segundos
        showConfirmButton: false
      });

      this.verFormUsuario.set(false); // Cerramos el formulario automáticamente
      this.limpiarFormUsuario();      // Limpiamos los campos
      this.cargarDatosSegunRol();     // Actualizamos el contador de "Comunidad"
    },
    error: (err) => {
      // ⚡ SweetAlert de error si el email ya existe o hay fallo en el server
      Swal.fire({
        title: 'Error en el alta',
        text: err.error?.message || 'No se pudo registrar al usuario. Revisa los datos.',
        icon: 'error',
        background: '#0f172a',
        color: '#ffffff',
        confirmButtonColor: '#ef4444'
      });
    }
  });
}
eliminarUsuario(id: number, nombre: string) {
  Swal.fire({
    title: `¿Eliminar a ${nombre}?`,
  }).then((result) => {
    if (result.isConfirmed) {
      // ⚠️ AQUÍ: 'usuarioService' debe estar inyectado en el constructor o como propiedad
      this.usuarioService.eliminarUsuario(id).subscribe({
        next: () => {
          Swal.fire('¡Eliminado!', '', 'success');
          this.cargarDatosSegunRol(); 
        }
      });
    }
  });
}
  limpiarFormUsuario() {
    this.nuevoUsuario = { nombre: '', email: '', password: '', rol: 'USUARIO' };
  }

  // --- GESTIÓN DE ZONAS (ADMIN) ---
  crearNuevaZona() {
    this.verFormZona.set(true);
    this.verFormUsuario.set(false);
    this.usuarioService.getUsuarios().subscribe(data => this.usuarios.set(data));
  }

 guardarZona() {
  const zonaAEnviar = { ...this.nuevaZona };
  if (!zonaAEnviar.usuario.id) {
      zonaAEnviar.usuario = null as any;
  }

  this.zonaService.crearZona(zonaAEnviar).subscribe({
    next: () => {
      Swal.fire({
        title: 'Zona Configurada',
        text: 'La parcela se ha vinculado al sistema correctamente.',
        icon: 'success',
        background: '#0f172a',
        color: '#ffffff',
        timer: 2000,
        showConfirmButton: false
      });
      this.verFormZona.set(false);
      this.cargarDatosSegunRol();
    },
    error: (err) => {
      Swal.fire('Error', 'No se pudo crear la zona', 'error');
    }
  });
}

 eliminarZona(id: number) {
  Swal.fire({
    title: '¿Eliminar parcela?',
    text: "Esta acción no se puede deshacer y se borrarán todos los registros.",
    icon: 'warning',
    showCancelButton: true,
    background: '#0f172a', // Color pizarra oscuro como tu fondo
    color: '#ffffff',
    confirmButtonColor: '#ef4444', // Rojo
    cancelButtonColor: '#334155', // Gris azulado
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar',
    customClass: {
      popup: 'border border-white/10 rounded-none', // Para que siga tu estilo cuadrado
    }
  }).then((result) => {
    if (result.isConfirmed) {
      this.zonaService.eliminarZona(id).subscribe({
        next: () => {
          Swal.fire({
            title: '¡Eliminada!',
            text: 'La zona ha sido borrada del sistema.',
            icon: 'success',
            background: '#0f172a',
            color: '#ffffff',
            confirmButtonColor: '#10b981'
          });
          this.cargarDatosSegunRol();
        },
        error: (err) => {
          Swal.fire('Error', 'No se pudo eliminar: ' + err.error, 'error');
        }
      });
    }
  });
}

  // --- CONTROL DE RIEGO (NUEVO) ---
  encenderRiego(idZona: number) {
    this.zonaService.iniciarRiego(idZona).subscribe({
      next: () => {
        console.log('Riego iniciado');
        this.cargarDatosSegunRol(); // Para ver si el estado de la zona cambia
      },
      error: (err) => alert('Error al iniciar riego: ' + err.error)
    });
  }

  apagarRiego(idZona: number) {
    this.zonaService.finalizarRiego(idZona).subscribe({
      next: () => {
        console.log('Riego finalizado');
        this.cargarDatosSegunRol();
      },
      error: (err) => alert('Error al finalizar riego: ' + err.error)
    });
  }


  toggleUsuarios() {
  this.verListaUsuarios.update(val => !val);
}
}