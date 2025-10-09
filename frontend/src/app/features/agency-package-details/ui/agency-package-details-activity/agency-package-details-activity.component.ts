import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { ActivityOrder } from '../../../../interfaces/orders/ActivityOrder';
import { DateUtils } from '../../../../utils/DateUtils';
import { TextComponent } from '../../../../shared/text.component';

@Component({
  selector: 'smt-agency-package-details-activity',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    ButtonModule,
    AvatarModule,
    TextComponent,
  ],
  templateUrl: './agency-package-details-activity.component.html',
  styles: `
    ::ng-deep
      .activity-review
      .p-accordion
      .p-accordion-header
      .p-accordion-toggle-icon {
      margin-left: auto;
      order: 2;
    }
    ::ng-deep
      .activity-review
      .p-accordion
      .p-accordion-header
      .p-accordion-header-link {
      padding: 0.5rem;
    }
  `,
})
export class AgencyPackageDetailsActivityComponent {
  dateUtils = new DateUtils();

  @Input({ alias: 'activity', required: true })
  activityOrder!: ActivityOrder;
  @Output() onViewActivity = new EventEmitter<ActivityOrder>();

  handleViewActivity(activity: ActivityOrder) {
    this.onViewActivity.emit(activity);
  }
}
