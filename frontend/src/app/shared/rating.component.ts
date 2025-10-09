import { Component, Input } from '@angular/core';

@Component({
  selector: 'smt-rating',
  standalone: true,
  imports: [],
  template: `
    <div class="surface-100 p-1" style="border-radius: 30px">
      <div
        class="surface-0 flex align-items-center gap-2 justify-content-center py-1 px-2"
        style="
        border-radius: 30px;
        box-shadow: 0px 1px 2px 0px rgba(0, 0, 0, 0.04),
                    0px 1px 2px 0px rgba(0, 0, 0, 0.06);
      "
      >
        @if (rating && rating > 0) {
          <span class="text-900 font-medium text-sm">{{ rating }}</span>
          <i class="pi pi-star-fill text-yellow-500"></i>
        } @else {
          <span class="text-900 font-medium text-sm">No ratings yet</span>
          <i class="pi pi-star"></i>
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class RatingComponent {
  @Input() rating?: number;
}
