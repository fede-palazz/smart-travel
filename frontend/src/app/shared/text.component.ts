import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'smt-text',
  standalone: true,
  imports: [CommonModule],
  template: `
    <p [ngClass]="[baseClasses, typeClasses[type], className]">
      <ng-content></ng-content>
    </p>
  `,
  styles: ``,
})
export class TextComponent {
  @Input() type!: 'title' | 'subtitle';
  @Input() className: string = '';

  baseClasses = 'block m-0'; // Common classes

  typeClasses: Record<'title' | 'subtitle', string> = {
    title: 'text-xl text-800 mt-2 mb-1',
    subtitle: 'text-md text-600 mb-1',
  };
}
