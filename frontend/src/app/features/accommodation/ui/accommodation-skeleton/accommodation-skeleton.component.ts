import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-accommodation-skeleton',
  standalone: true,
  imports: [CommonModule, SkeletonModule],
  templateUrl: './accommodation-skeleton.component.html',
  styles: ``,
})
export class AccommodationSkeletonComponent {
  @Input() repeat: number = 4;

  get replicaArray(): number[] {
    return Array.from({ length: this.repeat });
  }
}
