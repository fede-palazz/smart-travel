import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FilterContainerComponent } from '../../../../shared/filter-container.component';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AccommodationFilter } from '../../../../interfaces/filters/AccommodationFilter';
import { SliderModule } from 'primeng/slider';
import { RatingModule } from 'primeng/rating';
import { MultiSelectModule } from 'primeng/multiselect';

@Component({
  selector: 'smt-accommodation-filters',
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
  templateUrl: './accommodation-filters.component.html',
  styles: ``,
})
export class AccommodationFiltersComponent {
  // Default values
  minPrice: number = 20;
  maxPrice: number = 500;
  minDistanceKm: number = 0;
  maxDistanceKm: number = 25;
  types = [
    { name: 'Hotel', value: 'hotel' },
    { name: 'Apartment', value: 'apartment' },
    { name: 'B&B', value: 'b&b' },
  ];
  services = [
    { name: 'Parking', value: 'parking' },
    { name: 'Wi-Fi', value: 'wi-fi' },
    { name: 'Breakfast', value: 'breakfast' },
    { name: 'Self check-in', value: 'self-check-in' },
    { name: 'Lockers', value: 'lockers' },
    { name: 'SPA', value: 'spa' },
    { name: 'Restaurant', value: 'restaurant' },
    { name: 'Daily cleaning', value: 'daily-cleaning' },
    { name: 'Elevator', value: 'elevator' },
    { name: 'Laundry', value: 'laundry' },
    { name: 'Private kitchen', value: 'private kitchen' },
    { name: 'Private bathroom', value: 'private bathroom' },
    { name: 'Air conditioning', value: 'air conditioning' },
  ];

  // Filter values
  @Input() name?: string;
  @Input() minRating?: number = 0;
  @Input('types') selectedTypes?: string[];
  @Input('services') selectedServices?: string[];

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

  // Distance filter
  private _selectedMinRange?: number;
  private _selectedMaxRange?: number;

  @Input('minRange')
  set selectedMinRange(value: number | undefined) {
    this._selectedMinRange = value;
    this.updateDistanceRange();
  }

  @Input('maxRange')
  set selectedMaxRange(value: number | undefined) {
    this._selectedMaxRange = value;
    this.updateDistanceRange();
  }

  distanceRange: number[] = [this.minDistanceKm, this.maxDistanceKm];

  // Events
  @Output() onFilter = new EventEmitter<AccommodationFilter>();

  handleFilter() {
    const filters: AccommodationFilter = {
      name: this.name,
      types: this.selectedTypes,
      services: this.selectedServices,
      minDistanceToCenterKm: this.distanceRange[0],
      maxDistanceToCenterKm: this.distanceRange[1],
      minPricePerNight: this.priceRange[0],
      maxPricePerNight: this.priceRange[1],
      minRating: this.minRating,
    };
    this.onFilter.emit(filters);
  }

  handleReset() {
    this.name = undefined;
    this.selectedTypes = [];
    this.selectedServices = [];
    this.priceRange = [this.minPrice, this.maxPrice];
    this.distanceRange = [this.minDistanceKm, this.maxDistanceKm];
    this.minRating = 0;
  }

  /**
   * PRIVATE METHODS
   */

  private updatePriceRange(): void {
    if (this._selectedMinPrice != null && this._selectedMaxPrice != null) {
      this.priceRange = [this._selectedMinPrice, this._selectedMaxPrice];
    }
  }

  private updateDistanceRange(): void {
    if (this._selectedMinRange != null && this._selectedMaxRange != null) {
      this.distanceRange = [this._selectedMinRange, this._selectedMaxRange];
    }
  }
}
