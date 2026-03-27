import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (toastService.toastSignal(); as toast) {
      <div class="fixed top-5 right-5 z-[100] transition-all duration-500 transform translate-y-0">
        <div [ngClass]="{
          'bg-emerald-600': toast.type === 'success',
          'bg-red-600': toast.type === 'error',
          'bg-sky-600': toast.type === 'info'
        }" class="flex items-center space-x-3 px-6 py-4 rounded-xl shadow-2xl border-l-4 border-black/20 text-white min-w-[300px]">
          <span class="font-medium">{{ toast.message }}</span>
        </div>
      </div>
    }
  `
})
export class ToastComponent {
  public toastService = inject(ToastService);
}