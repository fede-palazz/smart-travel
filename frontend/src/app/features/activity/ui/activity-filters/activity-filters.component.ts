import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ActivityFilter } from '../../../../interfaces/filters/ActivityFilter';
import { CommonModule } from '@angular/common';
import { FilterContainerComponent } from '../../../../shared/filter-container.component';
import { FormsModule } from '@angular/forms';
import { MultiSelectModule } from 'primeng/multiselect';
import { SliderModule } from 'primeng/slider';
import { RatingModule } from 'primeng/rating';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'smt-activity-filters',
  standalone: true,
  imports: [
    CommonModule,
    FilterContainerComponent,
    FormsModule,
    MultiSelectModule,
    SliderModule,
    RatingModule,
    ButtonModule,
    InputTextModule,
  ],
  templateUrl: './activity-filters.component.html',
  styles: ``,
})
export class ActivityFiltersComponent {
  // Local variables
  minPrice: number = 1;
  maxPrice: number = 350;
  types = [
    { name: 'Museum Visit', value: 'museum' },
    { name: 'Guided Tour', value: 'tour' },
    { name: 'Cooking Class', value: 'cooking' },
    { name: 'Boat Ride', value: 'boat' },
    { name: 'Wine Tasting', value: 'wine' },
    { name: 'Concert', value: 'concert' },
    { name: 'Hiking Trip', value: 'hiking' },
    { name: 'Cycling Tour', value: 'cycling' },
    { name: 'Spa Session', value: 'spa' },
    { name: 'Art Workshop', value: 'workshop' },
  ];
  tags = [
    { name: 'Nature', value: 'nature' },
    { name: 'Food & Drink', value: 'food' },
    { name: 'Adventure', value: 'adventure' },
    { name: 'History', value: 'history' },
    { name: 'Romantic', value: 'romantic' },
    { name: 'Luxury', value: 'luxury' },
    { name: 'Cultural', value: 'culture' },
    { name: 'Wellness', value: 'wellness' },
    { name: 'Nightlife', value: 'nightlife' },
    { name: 'Family-Friendly', value: 'family' },
  ];
  languages = [
    { name: 'English', value: 'english' },
    { name: 'French', value: 'french' },
    { name: 'Spanish', value: 'spanish' },
    { name: 'German', value: 'german' },
    { name: 'Italian', value: 'italian' },
    { name: 'Portuguese', value: 'portoguese' },
  ];

  // Filter values
  @Input() name?: string;
  @Input() minRating?: number = 0;
  @Input('types') selectedTypes?: string[];
  @Input('tags') selectedTags?: string[];
  @Input('languages') selectedLanguages?: string[];

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

  // Events
  @Output() onFilter = new EventEmitter<ActivityFilter>();

  handleFilter() {
    const filters: ActivityFilter = {
      name: this.name,
      types: this.selectedTypes,
      tags: this.selectedTags,
      languages: this.selectedLanguages,
      minPrice: this.priceRange[0],
      maxPrice: this.priceRange[1],
      minRating: this.minRating,
    };
    this.onFilter.emit(filters);
  }

  handleReset() {
    this.name = undefined;
    this.selectedTypes = [];
    this.selectedTags = [];
    this.selectedLanguages = [];
    this.priceRange = [this.minPrice, this.maxPrice];
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
}
