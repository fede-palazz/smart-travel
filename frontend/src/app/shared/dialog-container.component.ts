import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { TextComponent } from './text.component';

@Component({
  selector: 'smt-dialog-container',
  standalone: true,
  imports: [CommonModule, DialogModule, TextComponent],
  template: `
    <p-dialog
      header="Header"
      [visible]="isVisible"
      [modal]="true"
      [draggable]="false"
      [dismissableMask]="true"
      [closeOnEscape]="true"
      (visibleChange)="handleClose()"
      [style]="{ width: width }"
    >
      <!-- Header -->
      <ng-template pTemplate="header">
        <smt-text type="title">{{ title }}</smt-text>
      </ng-template>

      <!-- Body -->
      <ng-content select="[dialog-body]" />

      <!-- Actions -->
      <ng-template pTemplate="footer">
        <ng-content select="[dialog-footer]"
      /></ng-template>
    </p-dialog>
  `,
  styles: ``,
})
export class DialogContainerComponent {
  // Local variables
  @Input({ required: true }) title!: string;
  @Input() width: string = '30rem';

  // Status variables
  @Input({ required: true }) isVisible!: boolean;

  // Events
  @Output() onClose = new EventEmitter();

  handleClose() {
    this.onClose.emit();
  }
}
