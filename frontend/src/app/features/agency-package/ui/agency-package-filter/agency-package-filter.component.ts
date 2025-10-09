import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';
import { RatingModule } from 'primeng/rating';
import { SliderModule } from 'primeng/slider';
import { FilterContainerComponent } from '../../../../shared/filter-container.component';
import { AgencyPackageFilter } from '../../../../interfaces/filters/AgencyPackageFilter';

@Component({
  selector: 'smt-agency-package-filter',
  standalone: true,
  imports: [
    FilterContainerComponent,
    ButtonModule,
    InputTextModule,
    FormsModule,
    CommonModule,
    SliderModule,
    RatingModule,
    MultiSelectModule,
  ],
  templateUrl: './agency-package-filter.component.html',
  styles: ``,
})
export class AgencyPackageFilterComponent {
  // Default values
  minPrice: number = 100;
  maxPrice: number = 3000;
  tags = [
    { name: 'Cruise', value: 'cruise' },
    { name: 'Luxury', value: 'luxury' },
    { name: 'Low cost', value: 'low_cost' },
    { name: 'Romantic', value: 'romantic' },
    { name: 'Adventure', value: 'adventure' },
    { name: 'Honeymoon', value: 'honeymoon' },
    { name: 'Group Tour', value: 'group_tour' },
    { name: 'Solo Travel', value: 'solo_travel' },
    { name: 'Last Minute', value: 'last_minute' },
    { name: 'Pet Friendly', value: 'pet_friendly' },
    { name: 'Wellness & Spa', value: 'wellness_spa' },
    { name: 'Family Friendly', value: 'family_friendly' },
    { name: 'Weekend Getaway', value: 'weekend_getaway' },
    { name: 'Cultural Experience', value: 'cultural_experience' },
  ];

  // Filter values
  @Input() name?: string;
  @Input('tags') selectedTags?: string[];

  // Price range filter
  private _selectedMinPrice?: number;
  private _selectedMaxPrice?: number;

  @Input('minPrice')
  set selectedMinPrice(value: number | undefined) {
    this._selectedMinPrice = value;
    this.updatePriceRange();
  }

  @Input('maxPrice')
  set selectedMaxPrice(value: number | undefined) {
    this._selectedMaxPrice = value;
    this.updatePriceRange();
  }

  priceRange: number[] = [this.minPrice, this.maxPrice];

  @Output() onFilter = new EventEmitter<AgencyPackageFilter>();

  handleFilter() {
    const filters: AgencyPackageFilter = {
      name: this.name,
      tags: this.selectedTags,
      minPrice: this.priceRange[0],
      maxPrice: this.priceRange[1],
    };
    this.onFilter.emit(filters);
  }

  handleReset() {
    this.name = undefined;
    this.selectedTags = [];
    this.priceRange = [this.minPrice, this.maxPrice];
  }

  /**
   * PRIVATE METHODS
   */

  private updatePriceRange(): void {
    if (this._selectedMinPrice != null && this._selectedMaxPrice != null) {
      this.priceRange = [this._selectedMinPrice, this._selectedMaxPrice];
    }
  }
}
