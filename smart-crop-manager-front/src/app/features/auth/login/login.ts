import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth'; // Asegúrate que la ruta apunte a auth.ts

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html', // Angular lo llamó login.html
  styleUrl: './login.scss'     // Angular lo llamó login.scss
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm = new FormGroup({
    username: new FormControl('', { 
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
        next: () => this.router.navigate(['/dashboard']),
        error: (err) => console.error('Error login:', err)
      });
    }
  }
}