import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-flight-list-skeleton',
  standalone: true,
  imports: [CommonModule, SkeletonModule],
  templateUrl: './flight-list-skeleton.component.html',
  styles: ``,
})
export class FlightListSkeletonComponent {
  @Input() repeat: number = 4;

  get replicaArray(): number[] {
    return Array.from({ length: this.repeat });
  }
}
