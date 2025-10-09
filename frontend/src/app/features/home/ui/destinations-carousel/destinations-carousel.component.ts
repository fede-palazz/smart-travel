import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CarouselModule } from 'primeng/carousel';
import { DestinationComponent } from '../destination/destination.component';
import { SkeletonModule } from 'primeng/skeleton';
import { DestinationPreview } from '../../../../interfaces/model/Destination';

@Component({
  selector: 'smt-destinations-carousel',
  standalone: true,
  imports: [CarouselModule, DestinationComponent, SkeletonModule],
  templateUrl: './destinations-carousel.component.html',
  styles: ``,
})
export class DestinationsCarouselComponent {
  @Input({ required: true }) destinations!: DestinationPreview[];

  responsiveOptions = [
    {
      breakpoint: '1799px',
      numVisible: 6,
      numScroll: 1,
    },
    {
      breakpoint: '1499px',
      numVisible: 5,
      numScroll: 1,
    },
    {
      breakpoint: '1199px',
      numVisible: 4,
      numScroll: 1,
    },
    {
      breakpoint: '991px',
      numVisible: 3,
      numScroll: 1,
    },
    {
      breakpoint: '767px',
      numVisible: 2,
      numScroll: 1,
    },
    {
      breakpoint: '467px',
      numVisible: 1,
      numScroll: 1,
    },
  ];

  // Events
  @Output() onViewDestination = new EventEmitter<DestinationPreview>();

  handleViewDestination(destination: DestinationPreview) {
    this.onViewDestination.emit(destination);
  }
}
