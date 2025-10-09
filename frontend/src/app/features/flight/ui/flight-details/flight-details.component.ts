import { Component, Input } from '@angular/core';
import { Flight } from '../../../../interfaces/model/Flight';
import { CommonModule } from '@angular/common';
import { TimelineModule } from 'primeng/timeline';
import { TextComponent } from '../../../../shared/text.component';
import { DateUtils } from '../../../../utils/DateUtils';

@Component({
  selector: 'smt-flight-details',
  standalone: true,
  imports: [CommonModule, TimelineModule, TextComponent],
  templateUrl: './flight-details.component.html',
  styles: `
    ::ng-deep div .p-timeline-event .p-timeline-event-opposite {
      display: none;
      width: 0;
    }
  `,
})
export class FlightDetailsComponent {
  @Input({ required: true }) flight!: Flight;

  dateUtils = new DateUtils();
}
