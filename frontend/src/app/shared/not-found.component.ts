import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { TextComponent } from './text.component';

@Component({
  selector: 'smt-not-found',
  standalone: true,
  imports: [CommonModule, TextComponent],
  template: `
    <div class="grid flex flex-column">
      <div class="col-6 col-offset-3 p-0 mt-3">
        <div class="border-round overflow-hidden">
          <img
            [src]="
              'assets/' +
              (type === 'error' ? type + '.jpg' : type + '_not_found.svg')
            "
            [alt]="getDescription()"
            class="w-full h-full object-cover"
          />
        </div>
        <div class="text-center mt-3">
          <smt-text type="title" className="font-bold">
            {{ getDescription() }}
          </smt-text>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class NotFoundComponent {
  @Input({ required: true }) type!:
    | 'package'
    | 'accommodation'
    | 'activity'
    | 'flight'
    | 'error';

  getDescription(): string {
    switch (this.type) {
      case 'package':
        return 'No packages found';
      case 'activity':
        return 'No activities found';
      case 'flight':
        return 'No flights found';
      case 'accommodation':
        return 'No stays found';
      case 'error':
        return 'An error occurred';
    }
  }
}
