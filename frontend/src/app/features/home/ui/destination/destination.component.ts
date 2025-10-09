import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { DestinationPreview } from '../../../../interfaces/model/Destination';

@Component({
  selector: 'smt-destination',
  standalone: true,
  imports: [CardModule, ButtonModule, TooltipModule],
  templateUrl: './destination.component.html',
  styles: `
    ::ng-deep .p-card-content {
      padding: 0px;
    }
  `,
})
export class DestinationComponent {
  @Input() dest!: DestinationPreview;

  // Events
  @Output() onViewDestination = new EventEmitter<DestinationPreview>();

  handleViewDestination() {
    this.onViewDestination.emit(this.dest);
  }
}
