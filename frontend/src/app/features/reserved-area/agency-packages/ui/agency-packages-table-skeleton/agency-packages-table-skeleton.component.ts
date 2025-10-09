import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-agency-packages-table-skeleton',
  standalone: true,
  imports: [CommonModule, SkeletonModule],
  templateUrl: './agency-packages-table-skeleton.component.html',
  styles: ``,
})
export class AgencyPackagesTableSkeletonComponent {
  @Input() rows: number = 5;

  get rowsArray(): number[] {
    return Array.from({ length: this.rows });
  }
}
