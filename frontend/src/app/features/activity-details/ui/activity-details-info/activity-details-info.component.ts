import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ChipModule } from 'primeng/chip';
import { FieldsetModule } from 'primeng/fieldset';
import { Activity } from '../../../../interfaces/model/Activity';

@Component({
  selector: 'smt-activity-details-info',
  standalone: true,
  imports: [ChipModule, CommonModule, FieldsetModule],
  templateUrl: './activity-details-info.component.html',
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
export class ActivityDetailsInfoComponent {
  @Input({ required: true }) activity!: Activity;
}
