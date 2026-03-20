import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth'; 
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet,CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
email = '';
  password = '';

  constructor(private authService: AuthService) {}

  onLogin() {
    console.log('Intentando login con:', this.email);
    
    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        console.log('¡Login correcto!', response);
        this.authService.saveToken(response.token);

        const nombreUsuario = response.nombre || this.email;
        alert(`Bienvenida al sistema, ${nombreUsuario}`);
      },
      error: (err) => {
        console.error('Error en el login', err);
        alert('Fallo al entrar: Revisa email o contraseña');
      }
    });
  }}
