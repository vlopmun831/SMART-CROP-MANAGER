import { Injectable, signal } from '@angular/core';

export interface Toast {
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  // Signal para manejar el estado global del mensaje
  toastSignal = signal<Toast | null>(null);

  show(message: string, type: 'success' | 'error' | 'info' = 'success') {
    this.toastSignal.set({ message, type });
    // Se limpia solo a los 3 segundos
    setTimeout(() => this.toastSignal.set(null), 3000);
  }
}