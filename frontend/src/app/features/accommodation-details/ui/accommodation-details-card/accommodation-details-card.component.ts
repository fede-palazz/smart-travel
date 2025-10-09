import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CardModule } from 'primeng/card';
import { SlideshowComponent } from '../../../../shared/slideshow.component';
import { TextComponent } from '../../../../shared/text.component';
import { AccommodationDetailsInfoComponent } from '../accommodation-details-info/accommodation-details-info.component';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { Accommodation } from '../../../../interfaces/model/Accommodation';
import { Room } from '../../../../interfaces/model/shared/Room';

@Component({
  selector: 'smt-accommodation-details-card',
  standalone: true,
  imports: [
    CardModule,
    CommonModule,
    SlideshowComponent,
    TextComponent,
    AccommodationDetailsInfoComponent,
    ReactiveFormsModule,
    TableModule,
    DropdownModule,
    ButtonModule,
    TagModule,
  ],
  templateUrl: './accommodation-details-card.component.html',
  styles: ``,
})
export class AccommodationDetailsCardComponent {
  // Status variables
  @Input({ required: true }) accommodationDetails!: Accommodation;
  @Input({ required: true }) form!: FormGroup;
  @Input({ required: true }) isAgentView!: boolean;

  // Events
  @Output() onCheckout = new EventEmitter<Accommodation>();

  // Used to generate options for dropdown [0, 1, ..., room.quantity]
  getQuantityOptions(max: number): { label: string; value: number }[] {
    return Array.from({ length: max + 1 }, (_, i) => ({
      label: i.toString(),
      value: i,
    }));
  }

  get roomQuantities(): FormArray {
    return this.form.get('roomQuantities') as FormArray;
  }

  getSelectedRooms(): Room[] {
    const quantities = this.roomQuantities.value;
    return this.accommodationDetails!.rooms.map((room, index) => ({
      ...room,
      quantity: quantities[index],
    })).filter((room) => room.quantity > 0);
  }

  handleCheckout() {
    this.onCheckout.emit(this.accommodationDetails);
  }
}
