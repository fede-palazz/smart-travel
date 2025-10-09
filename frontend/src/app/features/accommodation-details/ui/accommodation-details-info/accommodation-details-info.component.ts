import { Component, Input } from '@angular/core';
import { Accommodation } from '../../../../interfaces/model/Accommodation';
import { ChipModule } from 'primeng/chip';
import { CommonModule } from '@angular/common';
import { FieldsetModule } from 'primeng/fieldset';

@Component({
  selector: 'smt-accommodation-details-info',
  standalone: true,
  imports: [ChipModule, CommonModule, FieldsetModule],
  templateUrl: './accommodation-details-info.component.html',
  styles: `
    ::ng-deep .p-toggleable-content {
      padding: 0;
      width: 100%;
    }
    ::ng-deep .p-fieldset-content {
      padding: 0.5rem;
    }
    ::ng-deep .p-fieldset-legend {
      padding: 0.75rem 0.75rem;
    }
  `,
})
export class AccommodationDetailsInfoComponent {
  @Input({ required: true }) accommodationDetails!: Accommodation;
}
