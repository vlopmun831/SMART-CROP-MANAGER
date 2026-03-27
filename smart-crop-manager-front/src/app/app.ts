import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from './shared/components/toast/toast'; 

@Component({
  selector: 'app-root',
  standalone: true,
  // 2. Lo añadimos a los imports
  imports: [RouterOutlet, ToastComponent], 
  template: `
    <app-toast></app-toast> <router-outlet></router-outlet>
  `,
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('smart-crop-manager-front');
}