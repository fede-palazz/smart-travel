import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Activity } from '../../../../interfaces/model/Activity';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { DataViewModule } from 'primeng/dataview';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { RatingModule } from 'primeng/rating';
import { TagModule } from 'primeng/tag';
import { ActivityItemComponent } from '../activity-item/activity-item.component';

@Component({
  selector: 'smt-activity-list',
  standalone: true,
  imports: [
    DataViewModule,
    TagModule,
    RatingModule,
    ButtonModule,
    CommonModule,
    PaginatorModule,
    ActivityItemComponent,
    PaginatorModule,
  ],
  templateUrl: './activity-list.component.html',
  styles: ``,
})
export class ActivityListComponent {
  // State variables
  @Input() pageSize: number = 5;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;
  @Input({ required: true }) activities!: Activity[];

  // Events
  @Output() onSelectActivity = new EventEmitter<Activity>();
  @Output() onPageChange = new EventEmitter<number>();

  handleSelectActivity(activity: Activity) {
    this.onSelectActivity.emit(activity);
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }
}
