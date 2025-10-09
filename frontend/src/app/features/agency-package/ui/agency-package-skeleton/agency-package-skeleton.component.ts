import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-agency-package-skeleton',
  standalone: true,
  imports: [SkeletonModule, CommonModule],
  templateUrl: './agency-package-skeleton.component.html',
  styles: ``,
})
export class AgencyPackageSkeletonComponent {
  @Input() repeat: number = 4;

  get replicaArray(): number[] {
    return Array.from({ length: this.repeat });
  }
}
