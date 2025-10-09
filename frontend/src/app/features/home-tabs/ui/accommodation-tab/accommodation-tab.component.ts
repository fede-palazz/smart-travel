import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from 'primeng/autocomplete';
import { CalendarModule } from 'primeng/calendar';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { SuggestionService } from '../../services/suggestion.service';
import { AccommodationQueryParams } from '../../../../interfaces/params/AccommodationQueryParams';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  switchMap,
} from 'rxjs';
import { DropdownModule } from 'primeng/dropdown';
import { DestinationSuggestion } from '../../../../interfaces/filters/DestinationSearch';
@Component({
  selector: 'smt-accommodation-tab',
  standalone: true,
  imports: [
    IconFieldModule,
    InputIconModule,
    CalendarModule,
    InputTextModule,
    CommonModule,
    ReactiveFormsModule,
    AutoCompleteModule,
    DropdownModule,
  ],
  templateUrl: './accommodation-tab.component.html',
  styles: ``,
})
export class AccommodationTabComponent implements OnInit {
  // Local variables
  today: Date;
  accommodationForm!: FormGroup;
  guestOptions: any[] = [
    { label: '1', value: 1 },
    { label: '2', value: 2 },
    { label: '3', value: 3 },
    { label: '4', value: 4 },
    { label: '5', value: 5 },
  ];

  // State variables
  @Input() params?: AccommodationQueryParams;

  // Destination search suggestions
  private toSuggestions$ = new Subject<string>();
  toSuggestions: DestinationSuggestion[] = [];

  // Injectables
  private suggestionService = inject(SuggestionService);

  // Events
  @Output() onSearch = new EventEmitter<AccommodationQueryParams>();

  get f() {
    return this.accommodationForm.controls;
  }

  constructor() {
    // Set today's date
    const now = new Date();
    this.today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

    // Define destination search pipeline
    this.toSuggestions$
      .pipe(
        debounceTime(100),
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
  }

  ngOnInit() {
    // Define form group
    this.accommodationForm = new FormGroup({
      to: new FormControl(
        this.params?.to && this.params?.toType
          ? {
              name: this.params?.to,
              type: this.params?.toType,
            }
          : undefined,
        Validators.required,
      ),
      dates: new FormControl<Date[] | null>(
        this.params?.startDate && this.params?.endDate
          ? [new Date(this.params.startDate), new Date(this.params.endDate)]
          : null,
        [Validators.required, Validators.minLength(2), Validators.maxLength(2)],
      ),
      quantity: new FormControl(
        this.params?.quantity
          ? { label: `${this.params.quantity}`, value: this.params.quantity }
          : null,
        [Validators.required, Validators.min(1), Validators.max(5)],
      ),
    });
  }

  isToInvalid(): boolean {
    const toValue = this.accommodationForm.getRawValue().to;
    return !(
      toValue &&
      typeof toValue === 'object' &&
      toValue !== null &&
      'name' in toValue &&
      'type' in toValue &&
      typeof toValue.name === 'string' &&
      typeof toValue.type === 'string'
    );
  }

  onSearchTo(event: AutoCompleteCompleteEvent) {
    this.toSuggestions$.next(event.query);
  }

  isFormInvalid(): boolean {
    return this.accommodationForm.invalid || this.isToInvalid();
  }

  submit() {
    // Validate form
    if (this.isFormInvalid()) return;

    const formValues = this.accommodationForm.getRawValue();

    // Send form data as query params
    const queryParams: AccommodationQueryParams = {
      to: formValues.to.name,
      toType: formValues.to.type,
      startDate: formValues.dates[0].toISOString(),
      endDate: formValues.dates[1].toISOString(),
      quantity: formValues.quantity.value,
    };
    this.onSearch.emit(queryParams);
  }
}
