import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { GalleriaModule } from 'primeng/galleria';

@Component({
  selector: 'smt-slideshow',
  standalone: true,
  imports: [CommonModule, GalleriaModule],
  template: `
    <div
      class="border-round overflow-hidden"
      [ngClass]="!isFullScreen ? 'cursor-pointer' : ''"
    >
      <p-galleria
        [(visible)]="isFullScreen"
        [circular]="true"
        [fullScreen]="isFullScreen"
        [value]="pictures"
        [showItemNavigators]="true"
        [showItemNavigatorsOnHover]="false"
        [showIndicators]="false"
        [showThumbnails]="false"
        [numVisible]="1"
      >
        <ng-template pTemplate="item" let-item>
          <img
            [src]="item"
            [height]="isFullScreen ? fullscreenHeightPx : normalHeightPx"
            style="width: 100%; display: block"
            (click)="handleSwitchFullScreen()"
          />
        </ng-template>
      </p-galleria>
    </div>
  `,
  styles: ``,
})
export class SlideshowComponent {
  @Input({ required: true }) pictures!: string[];
  @Input() normalHeightPx: number = 250;
  @Input() fullscreenHeightPx: number = 600;
  @Input() isFullScreen: boolean = false;

  handleSwitchFullScreen() {
    this.isFullScreen = !this.isFullScreen;
  }
}
