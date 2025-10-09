import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FullAgencyPackageFilter } from '../../../../../interfaces/filters/AgencyPackageFilter';
import { SliderModule } from 'primeng/slider';
import { MultiSelectModule } from 'primeng/multiselect';
import { FormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { SelectButtonModule } from 'primeng/selectbutton';
import { PackageStatus } from '../../../../../interfaces/enums/PackageStatus';
import {
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  switchMap,
} from 'rxjs';
import { SuggestionService } from '../../../../home-tabs/services/suggestion.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from 'primeng/autocomplete';
import { User } from '../../../../../interfaces/model/User';
import { DialogContainerComponent } from '../../../../../shared/dialog-container.component';

@Component({
  selector: 'smt-agency-packages-filter',
  standalone: true,
  imports: [
    CommonModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    SliderModule,
    MultiSelectModule,
    FormsModule,
    CalendarModule,
    SelectButtonModule,
    AutoCompleteModule,
    DialogContainerComponent,
  ],
  templateUrl: './agency-packages-filter.component.html',
  styles: ``,
})
export class AgencyPackagesFilterComponent {
  // Local variables
  minPrice: number = 100;
  maxPrice: number = 15000;
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
  statusOptions = Object.values(PackageStatus).map((status) => ({
    label: status,
    value: status,
  }));

  // Status variables
  @Input({ required: true }) isVisible!: boolean;

  author?: User;
  selectedTags?: string[] = [];
  selectedStatus?: string;
  priceRange: number[] = [this.minPrice, this.maxPrice];
  startDate?: Date | null;
  endDate?: Date | null;
  to?: { name: string; type: string };

  // Destination and author search suggestions
  private toSuggestions$ = new Subject<string>();
  toSuggestions: { name: string; type: string }[] = [];
  private authorSuggestions$ = new Subject<string>();
  authorSuggestions: User[] = [];

  // Injectables
  private suggestionService = inject(SuggestionService);

  // Events
  @Output() onClose = new EventEmitter();
  @Output() onFilter = new EventEmitter<FullAgencyPackageFilter>();

  constructor() {
    // Define destination search pipeline
    this.toSuggestions$
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((term) =>
          this.suggestionService
            .getDestinationsSuggestions(term)
            .pipe(retry(2)),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(({ data: suggestions }) => {
        this.toSuggestions = suggestions ?? [];
      });
    // Define author search pipeline
    this.authorSuggestions$
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((term) =>
          this.suggestionService.getUsersSuggestions(term, true).pipe(retry(2)),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(({ data: suggestions }) => {
        this.authorSuggestions = suggestions ?? [];
      });
  }

  handleFilter() {
    const filters: FullAgencyPackageFilter = {
      to: !this.isToInvalid() ? this.to?.name : undefined,
      toType: !this.isToInvalid() ? this.to?.type : undefined,
      tags:
        this.selectedTags && this.selectedTags.length > 0
          ? this.selectedTags
          : undefined,
      status: this.selectedStatus ? this.selectedStatus : undefined,
      minPrice:
        this.priceRange[0] !== this.minPrice ? this.priceRange[0] : undefined,
      maxPrice:
        this.priceRange[1] !== this.maxPrice ? this.priceRange[1] : undefined,
      startDate: this.startDate ? this.startDate.toISOString() : undefined,
      endDate: this.endDate ? this.endDate.toISOString() : undefined,
      authorId: this.author ? this.author.id : undefined,
    };
    this.onFilter.emit(filters);
    this.onClose.emit();
  }

  handleReset() {
    this.to = undefined;
    this.startDate = null;
    this.endDate = null;
    this.selectedTags = [];
    this.priceRange = [this.minPrice, this.maxPrice];
    this.selectedStatus = undefined;
    this.author = undefined;
    this.onFilter.emit({});
  }

  handleClose() {
    this.onClose.emit();
  }

  onSearchTo(event: AutoCompleteCompleteEvent) {
    this.toSuggestions$.next(event.query);
  }

  onSearchAuthor(event: AutoCompleteCompleteEvent) {
    this.authorSuggestions$.next(event.query);
  }

  /**
   * PRIVATE METHODS
   */

  private isToInvalid(): boolean {
    return !(
      this.to &&
      typeof this.to === 'object' &&
      this.to !== null &&
      'name' in this.to &&
      'type' in this.to &&
      typeof this.to.name === 'string' &&
      typeof this.to.type === 'string'
    );
  }
}
