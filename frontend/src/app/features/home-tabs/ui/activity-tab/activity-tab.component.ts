import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import {
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  switchMap,
} from 'rxjs';
import { SuggestionService } from '../../services/suggestion.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivityQueryParams } from '../../../../interfaces/params/ActivityQueryParams';
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from 'primeng/autocomplete';
import { CommonModule } from '@angular/common';
import { DestinationSuggestion } from '../../../../interfaces/filters/DestinationSearch';

@Component({
  selector: 'smt-activity-tab',
  standalone: true,
  imports: [
    CommonModule,
    IconFieldModule,
    InputIconModule,
    CalendarModule,
    InputTextModule,
    ReactiveFormsModule,
    AutoCompleteModule,
  ],
  templateUrl: './activity-tab.component.html',
  styles: ``,
})
export class ActivityTabComponent {
  // Local variables
  today: Date;
  activityForm!: FormGroup;

  // State variables
  @Input() params?: ActivityQueryParams;

  // Destination search suggestions
  private toSuggestions$ = new Subject<string>();
  toSuggestions: DestinationSuggestion[] = [];

  // Injectables
  private suggestionService = inject(SuggestionService);

  // Events
  @Output() onSearch = new EventEmitter<ActivityQueryParams>();

  get f() {
    return this.activityForm.controls;
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
    this.activityForm = new FormGroup({
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
    });
  }

  isToInvalid(): boolean {
    const toValue = this.activityForm.getRawValue().to;
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
    return this.activityForm.invalid || this.isToInvalid();
  }

  submit() {
    // Validate form
    if (this.isFormInvalid()) return;

    const formValues = this.activityForm.getRawValue();

    // Send form data as query params
    const queryParams: ActivityQueryParams = {
      to: formValues.to.name,
      toType: formValues.to.type,
      startDate: formValues.dates[0].toISOString(),
      endDate: formValues.dates[1].toISOString(),
    };
    this.onSearch.emit(queryParams);
  }
}
