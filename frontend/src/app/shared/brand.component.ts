import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'smt-brand',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <a routerLink="/" class="cursor-pointer select-none">
      <div class="flex align-items-center justify-content-center">
        <div class="w-2rem h-2rem">
          <img src="assets/logo.svg" alt="Brand logo" class="w-full h-full" />
        </div>
        <span class="text-2xl my-0 ml-1 mr-3 white-space-nowrap">
          Smart Travel
        </span>
      </div>
    </a>
  `,
  styles: ``,
})
export class BrandComponent {}
