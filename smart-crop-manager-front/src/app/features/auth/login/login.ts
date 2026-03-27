import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
// 1. Importamos el ToastService
import { ToastService } from '../../../core/services/toast'; 

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService); // 2. Inyectamos el servicio

  loginForm = new FormGroup({
    email: new FormControl('', { 
      nonNullable: true, 
      validators: [Validators.required] 
    }),
    password: new FormControl('', { 
      nonNullable: true, 
      validators: [Validators.required] 
    })
  });

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.getRawValue()).subscribe({
        next: (response) => {
          // 3. Notificación de éxito
          this.toast.show(`¡Hola de nuevo, ${response.nombre}!`, 'success');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          // 4. Notificación de error elegante
          this.toast.show('Usuario o contraseña incorrectos', 'error');
          console.error('Error login:', err);
        }
      });
    }
  }
}