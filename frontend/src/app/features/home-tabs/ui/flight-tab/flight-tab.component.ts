import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { SelectButtonModule } from 'primeng/selectbutton';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { CommonModule } from '@angular/common';
import { FlightQueryParams } from '../../../../interfaces/params/FlightQueryParams';
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from 'primeng/autocomplete';
import {
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  switchMap,
} from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SuggestionService } from '../../services/suggestion.service';
import { DropdownModule } from 'primeng/dropdown';

@Component({
  selector: 'smt-flight-tab',
  standalone: true,
  imports: [
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    ButtonModule,
    SelectButtonModule,
    ReactiveFormsModule,
    CalendarModule,
    CommonModule,
    AutoCompleteModule,
    DropdownModule,
  ],
  templateUrl: './flight-tab.component.html',
  styles: ``,
})
export class FlightTabComponent implements OnInit {
  // Local variables
  today: Date;
  flightOptions = [
    { label: 'Return', value: 'return', icon: 'multiple_stop' },
    { label: 'One way', value: 'one-way', icon: 'trending_flat' },
  ];
  passengersOptions: any[] = [
    { label: '1', value: 1 },
    { label: '2', value: 2 },
    { label: '3', value: 3 },
    { label: '4', value: 4 },
    { label: '5', value: 5 },
  ];
  flightForm!: FormGroup;

  // State variables
  @Input() params?: FlightQueryParams;

  // Destination search suggestions
  private fromSuggestions$ = new Subject<string>();
  private toSuggestions$ = new Subject<string>();
  fromSuggestions: { name: string; type: string }[] = [];
  toSuggestions: { name: string; type: string }[] = [];

  // Injectables
  private suggestionService = inject(SuggestionService);

  // Events
  @Output() onSearch = new EventEmitter<FlightQueryParams>();

  get f() {
    return this.flightForm.controls;
  }

  constructor() {
    // Set today's date
    const now = new Date();
    this.today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

    // Define destination search pipeline
    this.fromSuggestions$
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
        this.fromSuggestions = suggestions ?? [];
      });
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
  }

  ngOnInit() {
    // Define form group
    this.flightForm = new FormGroup({
      type: new FormControl(this.params?.type ?? 'return', Validators.required),
      from: new FormControl(
        this.params?.from && this.params?.fromType
          ? {
              name: this.params?.from,
              type: this.params?.fromType,
            }
          : undefined,
        Validators.required,
      ),
      to: new FormControl(
        this.params?.to && this.params?.toType
          ? {
              name: this.params?.to,
              type: this.params?.toType,
            }
          : undefined,
        Validators.required,
      ),
      startDate: new FormControl(
        this.params?.startDate ? new Date(this.params.startDate) : null,
        Validators.required,
      ),
      endDate: new FormControl(
        this.params?.endDate ? new Date(this.params.endDate) : null,
      ),
      quantity: new FormControl(
        this.params?.quantity
          ? { label: `${this.params.quantity}`, value: this.params.quantity }
          : null,
        [Validators.required, Validators.min(1), Validators.max(5)],
      ),
    });
  }

  isReturnDateInvalid(): boolean {
    const formValues = this.flightForm.getRawValue();
    return (
      formValues.type === 'return' &&
      formValues.startDate &&
      formValues.endDate &&
      formValues.endDate < formValues.startDate
    );
  }

  isFromInvalid(): boolean {
    const fromValue = this.flightForm.getRawValue().from;
    return !(
      fromValue &&
      typeof fromValue === 'object' &&
      fromValue !== null &&
      'name' in fromValue &&
      'type' in fromValue &&
      typeof fromValue.name === 'string' &&
      typeof fromValue.type === 'string'
    );
  }

  isToInvalid(): boolean {
    const toValue = this.flightForm.getRawValue().to;
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

  isFormInvalid(): boolean {
    return (
      this.flightForm.invalid ||
      this.isFromInvalid() ||
      this.isToInvalid() ||
      this.isReturnDateInvalid()
    );
  }

  onSearchFrom(event: AutoCompleteCompleteEvent) {
    this.fromSuggestions$.next(event.query);
  }

  onSearchTo(event: AutoCompleteCompleteEvent) {
    this.toSuggestions$.next(event.query);
  }

  submit() {
    // Validate form
    if (this.isFormInvalid()) return;

    const formValues = this.flightForm.getRawValue();

    // Send form data as query params
    const queryParams: FlightQueryParams = {
      type: formValues.type,
      from: formValues.from.name,
      fromType: formValues.from.type,
      to: formValues.to.name,
      toType: formValues.to.type,
      startDate: formValues.startDate.toISOString(),
      endDate: formValues.endDate?.toISOString(),
      quantity: formValues.quantity.value,
    };
    this.onSearch.emit(queryParams);
  }
}
