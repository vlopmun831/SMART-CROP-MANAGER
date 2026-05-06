import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../core/services/auth';
import { ZonaService } from '../../core/services/zona/zona';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario/usuario';
import { AlertaService } from '../../core/services/alerta/alerta';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent implements OnInit {
  // ── Servicios ──────────────────────────────────────────────────────────────
  public authService = inject(AuthService);
  private zonaService = inject(ZonaService);
  private usuarioService = inject(UsuarioService);
  private alertaService = inject(AlertaService);

  // ── Datos ──────────────────────────────────────────────────────────────────
  public zonas = signal<any[]>([]);
  public usuarios = signal<any[]>([]);
  public alertasPendientes = signal<any[]>([]);

  // ── KPIs (admin) ──────────────────────────────────────────────────────────
  public totalZonas = signal<number>(0);
  public totalAlertasGlobales = signal<number>(0);
  public totalUsuarios = signal<number>(0);

  // ── Visibilidad de secciones (admin) ─────────────────────────────────────
  public verListaUsuarios = signal(false);
  public verFormUsuario = signal<boolean>(false);
  public verFormZona = signal<boolean>(false);
  public verFormEditarZona = signal<boolean>(false);

  // ── Vista activa usuario ──────────────────────────────────────────────────
  // 'dashboard' | 'historial'
  public vistaUsuario = signal<'dashboard' | 'historial'>('dashboard');
  public zonaSeleccionada = signal<any>(null);
  public historialDatos = signal<any[]>([]);
  public cargandoHistorial = signal(false);

  // ── Riego activo (guarda el ID del registro de riego devuelto por el back) ─
  // Map de zonaId → riegoId
  public riegoActivo = signal<Map<number, number>>(new Map());

  // ── Búsqueda de operario en el form de zona ───────────────────────────────
  busquedaOperario = signal('');
  confirmarPassword = signal('');

  operariosFiltrados = computed(() => {
    const query = this.busquedaOperario().toLowerCase();
    return this.usuarios().filter(u =>
      u.rol !== 'ADMIN' &&
      (u.nombre.toLowerCase().includes(query) || u.email.toLowerCase().includes(query))
    );
  });

  // ── Zona en edición ───────────────────────────────────────────────────────
  public zonaEditando: any = null;

  // ── Usuario en edición ────────────────────────────────────────────────────
  public usuarioEditando: any = null;
  public verFormEditarUsuario = signal<boolean>(false);

  // ── Formularios ───────────────────────────────────────────────────────────
  public nuevaZona = {
    varCultivo: '',
    ubicacion: '',
    humSueloMinConfig: '' as any,
    humSueloMaxConfig: '' as any,
    tempMaxConfig: '' as any,
    usuario: { id: null as any }
  };

  public nuevoUsuario = {
    nombre: '',
    email: '',
    password: '',
    rol: 'USUARIO'
  };

  // ── Pestaña activa ADMIN ──────────────────────────────────────────────────
  // 'zonas' | 'usuarios' | 'alertas'
  public pestanaAdmin = signal<'zonas' | 'usuarios' | 'alertas'>('zonas');

  // ──────────────────────────────────────────────────────────────────────────
  ngOnInit() {
    this.cargarDatosSegunRol();
  }

  cargarDatosSegunRol() {
    const rol = this.authService.userRole();
    const userId = this.authService.userId();

    if (rol === 'ADMIN') {
      this.zonaService.getZonas().subscribe(data => {
        this.zonas.set(data);
        this.totalZonas.set(data.length);
      });

      this.usuarioService.getUsuarios().subscribe(data => {
        this.usuarios.set(data);
        // Solo contamos operarios (no admins) para el KPI
        const operarios = data.filter((u: any) => u.rol !== 'ADMIN');
        this.totalUsuarios.set(operarios.length);
      });

      this.alertaService.getAlertasGlobales().subscribe(data => {
        this.alertasPendientes.set(data);
        const soloRojas = data.filter((a: any) => a.estado === 'PENDIENTE').length;
        this.totalAlertasGlobales.set(soloRojas);
      });

    } else if (rol === 'USUARIO' && userId) {
      this.zonaService.getZonasPorUsuario(userId).subscribe(data => this.zonas.set(data));
      this.alertaService.getAlertasPendientes(userId).subscribe(data => this.alertasPendientes.set(data));
    }
  }

  // ── Alertas ───────────────────────────────────────────────────────────────
  resolverAlerta(idAlerta: number) {
    this.alertaService.resolverAlerta(idAlerta).subscribe({
      next: () => {
        Swal.fire({
          title: '¡Resuelta!',
          text: 'La incidencia ha sido marcada como finalizada.',
          icon: 'success',
          timer: 1500,
          showConfirmButton: false,
          background: '#0f172a',
          color: '#ffffff'
        });
        this.cargarDatosSegunRol();
      },
      error: () => {
        Swal.fire({ title: 'Error', text: 'No se pudo actualizar el estado.', icon: 'error', background: '#0f172a', color: '#ffffff' });
      }
    });
  }

  // ── Usuarios (ADMIN) ──────────────────────────────────────────────────────
  crearNuevoUsuario() {
    this.verFormUsuario.set(true);
    this.verFormZona.set(false);
    this.verFormEditarZona.set(false);
  }

  guardarUsuario() {
    if (this.nuevoUsuario.password !== this.confirmarPassword()) {
      Swal.fire({ icon: 'error', title: 'Error de validación', text: 'Las contraseñas no coinciden.', background: '#0f172a', color: '#ffffff' });
      return;
    }
    this.usuarioService.crearUsuario(this.nuevoUsuario).subscribe({
      next: () => {
        Swal.fire({ title: '¡Registro Exitoso!', text: `El operador ${this.nuevoUsuario.nombre} ha sido dado de alta.`, icon: 'success', background: '#0f172a', color: '#ffffff', confirmButtonColor: '#10b981', timer: 2000, showConfirmButton: false });
        this.verFormUsuario.set(false);
        this.limpiarFormUsuario();
        this.cargarDatosSegunRol();
      },
      error: (err) => {
        Swal.fire({ title: 'Error en el alta', text: err.error?.message || 'No se pudo registrar al usuario.', icon: 'error', background: '#0f172a', color: '#ffffff', confirmButtonColor: '#ef4444' });
      }
    });
  }

  editarUsuario(u: any) {
    this.usuarioEditando = { id: u.id, nombre: u.nombre, email: u.email, password: '' };
    this.verFormEditarUsuario.set(true);
    this.verFormUsuario.set(false);
  }

  guardarEdicionUsuario() {
    const payload = { ...this.usuarioEditando };
    // Si no se escribio password, no lo enviamos
    if (!payload.password) delete payload.password;
    this.usuarioService.modificarUsuario(payload.id, payload).subscribe({
      next: () => {
        Swal.fire({ title: 'Operario actualizado', icon: 'success', timer: 1800, showConfirmButton: false, background: '#0f172a', color: '#ffffff' });
        this.verFormEditarUsuario.set(false);
        this.usuarioEditando = null;
        this.cargarDatosSegunRol();
      },
      error: (err) => {
        Swal.fire({ title: 'Error', text: err.error?.message || 'No se pudo guardar los cambios.', icon: 'error', background: '#0f172a', color: '#ffffff' });
      }
    });
  }

  eliminarUsuario(id: number, nombre: string) {
    Swal.fire({
      title: `¿Eliminar a ${nombre}?`,
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      background: '#0f172a',
      color: '#ffffff',
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#334155',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.usuarioService.eliminarUsuario(id).subscribe({
          next: () => {
            Swal.fire({ title: '¡Eliminado!', icon: 'success', background: '#0f172a', color: '#ffffff', timer: 1500, showConfirmButton: false });
            this.cargarDatosSegunRol();
          }
        });
      }
    });
  }

  limpiarFormUsuario() {
    this.nuevoUsuario = { nombre: '', email: '', password: '', rol: 'USUARIO' };
    this.confirmarPassword.set('');
  }

  // ── Zonas (ADMIN) ─────────────────────────────────────────────────────────
  crearNuevaZona() {
    this.verFormZona.set(true);
    this.verFormUsuario.set(false);
    this.verFormEditarZona.set(false);
    this.usuarioService.getUsuarios().subscribe(data => this.usuarios.set(data));
  }

  guardarZona() {
    const zonaAEnviar = { ...this.nuevaZona };
    if (!zonaAEnviar.usuario.id) {
      zonaAEnviar.usuario = null as any;
    }
    this.zonaService.crearZona(zonaAEnviar).subscribe({
      next: () => {
        Swal.fire({ title: 'Zona Configurada', text: 'La parcela se ha vinculado al sistema correctamente.', icon: 'success', background: '#0f172a', color: '#ffffff', timer: 2000, showConfirmButton: false });
        this.verFormZona.set(false);
        this.limpiarFormZona();
        this.cargarDatosSegunRol();
      },
      error: () => Swal.fire('Error', 'No se pudo crear la zona', 'error')
    });
  }

  limpiarFormZona() {
    this.nuevaZona = { varCultivo: '', ubicacion: '', humSueloMinConfig: '', humSueloMaxConfig: '', tempMaxConfig: '', usuario: { id: null } };
    this.busquedaOperario.set('');
  }

  editarZona(zona: any) {
    this.zonaEditando = { ...zona, usuario: zona.idUsuario ? { id: zona.idUsuario } : null };
    this.verFormEditarZona.set(true);
    this.verFormZona.set(false);
    this.verFormUsuario.set(false);
    this.busquedaOperario.set(zona.nombreUsuario ?? '');
    this.usuarioService.getUsuarios().subscribe(data => this.usuarios.set(data));
  }

  guardarEdicionZona() {
    const payload = { ...this.zonaEditando };
    this.zonaService.modificarZona(payload.id, payload).subscribe({
      next: () => {
        Swal.fire({ title: '¡Actualizada!', text: 'La zona ha sido modificada.', icon: 'success', background: '#0f172a', color: '#ffffff', timer: 1800, showConfirmButton: false });
        this.verFormEditarZona.set(false);
        this.zonaEditando = null;
        this.cargarDatosSegunRol();
      },
      error: () => Swal.fire('Error', 'No se pudo guardar los cambios.', 'error')
    });
  }

  eliminarZona(id: number) {
    Swal.fire({
      title: '¿Eliminar parcela?',
      text: 'Esta acción no se puede deshacer y borrará todos los registros.',
      icon: 'warning',
      showCancelButton: true,
      background: '#0f172a',
      color: '#ffffff',
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#334155',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.zonaService.eliminarZona(id).subscribe({
          next: () => {
            Swal.fire({ title: '¡Eliminada!', text: 'La zona ha sido borrada del sistema.', icon: 'success', background: '#0f172a', color: '#ffffff', confirmButtonColor: '#10b981' });
            this.cargarDatosSegunRol();
          },
          error: (err) => Swal.fire('Error', 'No se pudo eliminar: ' + err.error, 'error')
        });
      }
    });
  }

  seleccionarOperario(usuario: any) {
    if (this.verFormEditarZona()) {
      this.zonaEditando.usuario = { id: usuario.id };
    } else {
      this.nuevaZona.usuario.id = usuario.id;
    }
    this.busquedaOperario.set(usuario.nombre);
  }

  toggleUsuarios() {
    this.verListaUsuarios.update(val => !val);
  }

  // ── Riego (USUARIO) ───────────────────────────────────────────────────────
  encenderRiego(idZona: number) {
    this.zonaService.iniciarRiego(idZona).subscribe({
      next: (riego: any) => {
        // Guardamos el ID del registro de riego para poder finalizarlo
        const mapa = new Map(this.riegoActivo());
        mapa.set(idZona, riego.id);
        this.riegoActivo.set(mapa);
        Swal.fire({ title: '💧 Riego Iniciado', text: 'El sistema de riego está activo.', icon: 'success', timer: 1500, showConfirmButton: false, background: '#0f172a', color: '#ffffff' });
      },
      error: (err) => Swal.fire('Error', 'No se pudo iniciar el riego: ' + (err.error ?? ''), 'error')
    });
  }

  apagarRiego(idZona: number) {
    const idRiego = this.riegoActivo().get(idZona);
    if (!idRiego) {
      Swal.fire({ title: 'Sin riego activo', text: 'No hay ningún riego iniciado para esta zona.', icon: 'info', background: '#0f172a', color: '#ffffff' });
      return;
    }
    this.zonaService.finalizarRiego(idRiego).subscribe({
      next: () => {
        const mapa = new Map(this.riegoActivo());
        mapa.delete(idZona);
        this.riegoActivo.set(mapa);
        Swal.fire({ title: 'Riego Detenido', icon: 'info', timer: 1500, showConfirmButton: false, background: '#0f172a', color: '#ffffff' });
      },
      error: (err) => Swal.fire('Error', 'No se pudo detener el riego.', 'error')
    });
  }

  tieneRiegoActivo(idZona: number): boolean {
    return this.riegoActivo().has(idZona);
  }

  // ── Historial de datos de sensores (USUARIO) ──────────────────────────────
  verHistorial(zona: any) {
    this.zonaSeleccionada.set(zona);
    this.vistaUsuario.set('historial');
    this.cargandoHistorial.set(true);
    this.zonaService.getHistorialDatos(zona.id).subscribe({
      next: (data) => {
        if (data.length > 0) {
          console.log('CAMPOS DEL REGISTRO (primer elemento):', JSON.stringify(data[0]));
        }
        this.historialDatos.set(data);
        this.cargandoHistorial.set(false);
      },
      error: () => {
        this.historialDatos.set([]);
        this.cargandoHistorial.set(false);
      }
    });
  }

  volverAlDashboard() {
    this.vistaUsuario.set('dashboard');
    this.zonaSeleccionada.set(null);
    this.historialDatos.set([]);
  }

  // ── Helpers ───────────────────────────────────────────────────────────────
  getHumedadClass(valor: number | null): string {
    if (valor === null || valor === undefined) return 'text-slate-500';
    if (valor < 30) return 'text-red-400';
    if (valor > 80) return 'text-blue-400';
    return 'text-emerald-400';
  }

  getBarWidth(valor: number | null): number {
    if (!valor) return 0;
    return Math.min(100, Math.max(0, valor));
  }
  // Parsea fechas del backend: "19-03-2026 20:12:42" (DD-MM-YYYY HH:mm:ss)
  // También maneja arrays [y,mo,d,h,m,s] y strings ISO
  parseDate(fecha: any): Date | null {
    if (!fecha) return null;
    let result: Date;
    if (Array.isArray(fecha)) {
      const [y, mo, d, h = 0, m = 0, s = 0] = fecha;
      result = new Date(y, mo - 1, d, h, m, s);
    } else {
      const str = String(fecha);
      // Formato DD-MM-YYYY HH:mm:ss  ó  DD-MM-YYYY HH:mm
      const match = str.match(/^(\d{2})-(\d{2})-(\d{4})\s(\d{2}):(\d{2})(?::(\d{2}))?$/);
      if (match) {
        const [, d, mo, y, h, m, s = '0'] = match;
        result = new Date(+y, +mo - 1, +d, +h, +m, +s);
      } else {
        result = new Date(str);
      }
    }
    return isNaN(result.getTime()) ? null : result;
  }
}
