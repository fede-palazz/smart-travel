import { Component } from '@angular/core';

@Component({
  selector: 'smt-footer',
  standalone: true,
  imports: [],
  template: `
    <footer
      class="pt-4 pb-2 px-3 md:px-0 text-sm text-gray-600 flex justify-content-between"
    >
      <div class="mb-2">
        © 2025 Smart Travel | Made with ❤️ by Federico Palazzi
      </div>
      <div class="flex justify-center gap-4 text-gray-500">
        <!-- GitHub profile -->
        <a
          href="https://github.com/fede-palazz"
          target="_blank"
          aria-label="GitHub"
          class="hover:text-gray-800"
        >
          <i class="pi pi-github text-lg"></i>
        </a>

        <!-- LinkedIn profile -->
        <a
          href="https://linkedin.com/in/fede-palazz"
          target="_blank"
          aria-label="LinkedIn"
          class="hover:text-gray-800"
        >
          <i class="pi pi-linkedin text-lg"></i>
        </a>
      </div>
    </footer>
  `,
  styles: ``,
})
export class FooterComponent {}
