import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-activity-skeleton',
  standalone: true,
  imports: [CommonModule, SkeletonModule],
  templateUrl: './activity-skeleton.component.html',
  styles: ``,
})
export class ActivitySkeletonComponent {
  @Input() repeat: number = 4;

  get replicaArray(): number[] {
    return Array.from({ length: this.repeat });
  }
}
