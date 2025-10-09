import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Activity } from '../../../../interfaces/model/Activity';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'smt-activity-item',
  standalone: true,
  imports: [CommonModule, ButtonModule, TagModule],
  templateUrl: './activity-item.component.html',
  styles: ``,
})
export class ActivityItemComponent {
  @Input({ required: true }) activity!: Activity;
  @Input() isFirst = false;
  @Output() onSelectActivity = new EventEmitter<Activity>();

  handleSelectActivity() {
    this.onSelectActivity.emit(this.activity);
  }
}
