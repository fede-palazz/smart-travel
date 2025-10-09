import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
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
import { AgencyPackageQueryParams } from '../../../../interfaces/params/AgencyPackageQueryParams';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SuggestionService } from '../../services/suggestion.service';
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from 'primeng/autocomplete';

@Component({
  selector: 'smt-agency-package-tab',
  standalone: true,
  imports: [
    AutoCompleteModule,
    CommonModule,
    CalendarModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    InputIconModule,
    IconFieldModule,
  ],
  templateUrl: './agency-package-tab.component.html',
  styles: ``,
})
export class AgencyPackageTabComponent {
  // Local variables
  today: Date;
  packageForm!: FormGroup;
  @Input() showCustomize: boolean = true;

  // State variables
  @Input() params?: AgencyPackageQueryParams;

  // Destination search suggestions
  private toSuggestions$ = new Subject<string>();
  toSuggestions: { name: string; type: string }[] = [];

  // Injectables
  private suggestionService = inject(SuggestionService);

  // Events
  @Output() onSearch = new EventEmitter<AgencyPackageQueryParams>();
  @Output() onCustomizePackage = new EventEmitter();

  get f() {
    return this.packageForm.controls;
  }

  constructor() {
    // Set today's date
    const now = new Date();
    this.today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

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
  }

  ngOnInit() {
    // Define form group
    this.packageForm = new FormGroup({
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
    const toValue = this.packageForm.getRawValue().to;
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

  handleCustomizePackage() {
    this.onCustomizePackage.emit();
  }

  isFormInvalid(): boolean {
    return this.packageForm.invalid || this.isToInvalid();
  }

  submit() {
    // Validate form
    if (this.isFormInvalid()) return;

    const formValues = this.packageForm.getRawValue();

    // Send form data as query params
    const queryParams: AgencyPackageQueryParams = {
      to: formValues.to.name,
      toType: formValues.to.type,
      startDate: formValues.dates[0].toISOString(),
      endDate: formValues.dates[1].toISOString(),
    };
    this.onSearch.emit(queryParams);
  }
}
