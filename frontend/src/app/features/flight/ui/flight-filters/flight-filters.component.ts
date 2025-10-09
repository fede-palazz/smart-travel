import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { SliderModule } from 'primeng/slider';
import { Flight } from '../../../../interfaces/model/Flight';
import { InputTextModule } from 'primeng/inputtext';
import { CommonModule } from '@angular/common';
import { DropdownModule } from 'primeng/dropdown';
import { FlightFilter } from '../../../../interfaces/filters/FlightFilter';
import { FilterContainerComponent } from '../../../../shared/filter-container.component';

@Component({
  selector: 'smt-flight-filters',
  standalone: true,
  imports: [
    CardModule,
    ButtonModule,
    SliderModule,
    FormsModule,
    InputTextModule,
    CommonModule,
    DropdownModule,
    FilterContainerComponent,
  ],
  templateUrl: './flight-filters.component.html',
  styles: ``,
})
export class FlightFiltersComponent {
  // Default values
  minPrice: number = 5;
  maxPrice: number = 500;

  // Flights input
  private _flights: Flight[] = [];
  @Input({ required: true })
  set flights(value: Flight[]) {
    this._flights = value;
    if (value && !!value.length) this.updateAirlines();
  }

  get flights(): Flight[] {
    return this._flights;
  }

  // Airline filter
  private _selectedAirlineName?: string;
  @Input({ required: true, alias: 'airline' })
  set selectedAirlineName(value: string | undefined) {
    this._selectedAirlineName = value;
    this.updateSelectedAirline();
  }

  get selectedAirlineName(): string | undefined {
    return this._selectedAirlineName;
  }

  airlines: { name: string; logo: string }[] = [];
  selectedAirline?: { name: string; logo: string };

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
  @Output() onFilter = new EventEmitter<FlightFilter>();

  handleFilter() {
    const filters: FlightFilter = {
      airline: this.selectedAirline ? this.selectedAirline.name : undefined,
      minPrice: this.priceRange[0],
      maxPrice: this.priceRange[1],
    };
    this.onFilter.emit(filters);
  }

  handleReset() {
    this.selectedAirline = undefined;
    this.priceRange = [this.minPrice, this.maxPrice];
  }

  /**
   * PRIVATE METHODS
   */

  // Update the airline list when a new set of flights is loaded
  private updateAirlines() {
    const airlineNames = Array.from(
      new Set(this.flights.map((f) => f.airline)),
    );
    this.airlines = airlineNames.map((name) => {
      const logo =
        this.flights.find((f) => f.airline === name)?.airlineLogo ?? '';
      return { name, logo };
    });
    this.updateSelectedAirline();
  }

  private updateSelectedAirline(): void {
    const selectedAirlineLogo = this.airlines.find(
      (airline) => airline.name === this.selectedAirlineName,
    )?.logo;
    this.selectedAirline =
      this.selectedAirlineName && selectedAirlineLogo
        ? { name: this.selectedAirlineName, logo: selectedAirlineLogo }
        : undefined;
  }

  private updatePriceRange(): void {
    if (this._selectedMinPrice != null && this._selectedMaxPrice != null) {
      this.priceRange = [this._selectedMinPrice, this._selectedMaxPrice];
    }
  }
}
