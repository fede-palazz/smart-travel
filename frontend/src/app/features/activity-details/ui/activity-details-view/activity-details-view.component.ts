import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CarouselModule } from 'primeng/carousel';
import { ChipModule } from 'primeng/chip';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { QuickActionsBarComponent } from '../../../../shared/quick-actions-bar.component';
import {
  ActivityCheckoutSelection,
  ActivityDetailsCardComponent,
} from '../activity-details-card/activity-details-card.component';
import { map, Observable } from 'rxjs';
import { Activity } from '../../../../interfaces/model/Activity';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { ActivityOrder } from '../../../../interfaces/orders/ActivityOrder';
import { ActivityDetailsService } from '../../services/activity-details.service';
import { NotFoundComponent } from '../../../../shared/not-found.component';
import { AuthService } from '../../../../services/auth.service';
import { UserRole } from '../../../../interfaces/model/User';

@Component({
  selector: 'smt-activity-details-view',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    TableModule,
    ChipModule,
    CarouselModule,
    ButtonModule,
    ToastModule,
    DropdownModule,
    InputNumberModule,
    ReactiveFormsModule,
    ActivityDetailsCardComponent,
    QuickActionsBarComponent,
    NotFoundComponent,
  ],
  templateUrl: './activity-details-view.component.html',
  styles: ``,
})
export class ActivityDetailsViewComponent implements OnInit {
  // Status variables
  @Input({ required: true }) activityId!: string;
  activityDetails$!: Observable<QueryResult<Activity>>;
  @Input() isAgentView: boolean = false;

  // Injectables
  private activityDetailsService = inject(ActivityDetailsService);

  // Events
  @Output() onNavigateBack = new EventEmitter();
  @Output() onCheckout = new EventEmitter<ActivityOrder>();

  ngOnInit(): void {
    this.activityDetails$ = this.activityDetailsService.getActivityDetails(
      this.activityId,
    );
  }

  handleNavigateBack(): void {
    this.onNavigateBack.emit();
  }

  handleCheckout(activitySelection: ActivityCheckoutSelection) {
    const activity = activitySelection.activity;

    const activityOrder: ActivityOrder = {
      activityId: activity.id,
      name: activity.name,
      type: activity.type,
      mainPicture: activity.mainPicture,
      date: activitySelection.date,
      price: activity.price,
      startTime: activity.schedule.recurrence.startTime,
      endTime: activity.schedule.recurrence.endTime,
      quantity: activitySelection.peopleNum,
    };
    this.onCheckout.emit(activityOrder);
  }
}
