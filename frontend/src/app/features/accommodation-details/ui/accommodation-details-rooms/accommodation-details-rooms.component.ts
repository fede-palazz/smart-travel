import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ReactiveFormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { Accommodation } from '../../../../interfaces/model/Accommodation';
import { Room } from '../../../../interfaces/model/shared/Room';

@Component({
  selector: 'smt-accommodation-details-rooms',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    ReactiveFormsModule,
    DropdownModule,
  ],
  templateUrl: './accommodation-details-rooms.component.html',
  styles: ``,
})
export class AccommodationDetailsRoomsComponent {
  @Input({ required: true }) rooms!: Room[];
  @Input() readonly: boolean = false;
  @Input() displayPrice: boolean = true;

  // Used to generate options for dropdown [0, 1, ..., room.quantity]
  getQuantityOptions(max: number): { label: string; value: number }[] {
    return Array.from({ length: max + 1 }, (_, i) => ({
      label: i.toString(),
      value: i,
    }));
  }
}
