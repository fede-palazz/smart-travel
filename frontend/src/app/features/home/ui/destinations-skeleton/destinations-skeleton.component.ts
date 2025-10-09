import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-destinations-skeleton',
  standalone: true,
  imports: [SkeletonModule, CommonModule],
  templateUrl: './destinations-skeleton.component.html',
  styles: ``,
})
export class DestinationsSkeletonComponent {
  @Input() repeat: number = 3;

  get replicaArray(): number[] {
    return Array.from({ length: this.repeat });
  }
}
