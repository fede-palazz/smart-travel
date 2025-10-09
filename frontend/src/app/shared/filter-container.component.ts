import { Component } from '@angular/core';
import { CardModule } from 'primeng/card';
import { TextComponent } from './text.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'smt-filter-container',
  standalone: true,
  imports: [CardModule, TextComponent, CommonModule],
  template: `
    <p-card styleClass="w-full">
      <!-- Title -->
      <ng-template pTemplate="header">
        <div class="pt-4 px-4 m-0">
          <smt-text type="title">Filters</smt-text>
        </div>
      </ng-template>

      <!-- Body -->
      <ng-content select="[filter-body]" />

      <!-- Footer -->
      <ng-template pTemplate="footer">
        <ng-content select="[filter-footer]" />
      </ng-template>
    </p-card>
  `,
  styles: ``,
})
export class FilterContainerComponent {}
