import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'smt-quick-actions-bar',
  standalone: true,
  imports: [ButtonModule, ToastModule],
  styles: ``,
  template: `
    <div class="flex w-full justify-content-between gap-2 my-3">
      <p-button
        icon="pi pi-arrow-left"
        severity="secondary"
        text="true"
        label="Back to results"
        (onClick)="handleNavigateBack()"
        styleClass="text-700"
      />
      <div class="flex gap-2">
        <p-button
          icon="pi pi-share-alt"
          severity="info"
          label="Share"
          (onClick)="handleShareUrl()"
        />
      </div>
    </div>
    <p-toast />
  `,
})
export class QuickActionsBarComponent {
  // Injectables
  private messageService = inject(MessageService);

  // Events
  @Output() onNavigateBack = new EventEmitter();

  handleShareUrl() {
    const url = window.location.href;
    navigator.clipboard
      .writeText(url)
      .then(() => {
        this.messageService.add({
          severity: 'info',
          summary: 'Link copied!',
          detail: 'The URL has been copied to your clipboard.',
        });
      })
      .catch(() => {
        this.messageService.add({
          severity: 'error',
          summary: 'Copy failed',
          detail: 'Unable to copy the URL.',
        });
      });
  }

  handleNavigateBack() {
    this.onNavigateBack.emit();
  }
}
