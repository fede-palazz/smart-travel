import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { ActivityOrder } from '../../../../interfaces/orders/ActivityOrder';
import { TextComponent } from '../../../../shared/text.component';

@Component({
  selector: 'smt-activity-review',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    ButtonModule,
    AvatarModule,
    TextComponent,
  ],
  templateUrl: './activity-review.component.html',
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
export class ActivityReviewComponent {
  @Input() hideAction: boolean = false;
  @Input({ alias: 'activity', required: true })
  activityOrder!: ActivityOrder;
  @Output() onDeleteActivity = new EventEmitter<string>();

  getActivityTotalPrice(): number {
    return this.activityOrder.price.value * this.activityOrder.quantity;
  }

  handleDeleteActivity(id: string) {
    this.onDeleteActivity.emit(id);
  }
}
