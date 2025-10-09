import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'smt-agency-packages-card-skeleton',
  standalone: true,
  imports: [CommonModule, SkeletonModule],
  templateUrl: './agency-packages-card-skeleton.component.html',
  styles: ``,
})
export class AgencyPackagesCardSkeletonComponent {}
