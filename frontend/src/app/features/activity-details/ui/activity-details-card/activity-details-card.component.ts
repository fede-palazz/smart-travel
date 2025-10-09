import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CardModule } from 'primeng/card';
import { Activity } from '../../../../interfaces/model/Activity';
import { SlideshowComponent } from '../../../../shared/slideshow.component';
import { TextComponent } from '../../../../shared/text.component';
import { ActivityDetailsInfoComponent } from '../activity-details-info/activity-details-info.component';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';

export interface ActivityCheckoutSelection {
  activity: Activity;
  date: string;
  peopleNum: number;
}

@Component({
  selector: 'smt-activity-details-card',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    TextComponent,
    ActivityDetailsInfoComponent,
    SlideshowComponent,
    ButtonModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
  ],
  templateUrl: './activity-details-card.component.html',
  styles: ``,
})
export class ActivityDetailsCardComponent implements OnInit {
  // Local variables
  minDate?: Date;
  maxDate?: Date;
  peopleOptions: any[] = [
    { label: '1', value: 1 },
    { label: '2', value: 2 },
    { label: '3', value: 3 },
    { label: '4', value: 4 },
    { label: '5', value: 5 },
  ];

  // State variables
  selectedDate?: Date;
  selectedPeopleNum?: any;

  @Input({ required: true }) isAgentView!: boolean;
  @Input({ required: true }) activity!: Activity;
  @Output() onCheckout = new EventEmitter<ActivityCheckoutSelection>();

  ngOnInit() {
    const today = new Date();
    const startDate = new Date(this.activity.schedule.startDate);
    this.minDate = startDate.getTime() < today.getTime() ? today : startDate;
    this.maxDate = new Date(this.activity.schedule.endDate);
  }

  handleCheckout() {
    if (!this.selectedPeopleNum || !this.selectedDate) return;

    this.onCheckout.emit({
      activity: this.activity,
      date: this.selectedDate.toISOString(),
      peopleNum: this.selectedPeopleNum.value, // FIXME: Use reactive forms
    });
  }
}
