import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { SelectButtonModule } from 'primeng/selectbutton';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  retry,
} from 'rxjs';
import { CheckboxModule } from 'primeng/checkbox';
import { SuggestionService } from '../../services/suggestion.service';
import { CustomPackageQueryParams } from '../../../../interfaces/params/CustomPackageQueryParams';

@Component({
  selector: 'smt-custom-package-tab',
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
    CheckboxModule,
  ],
  templateUrl: './custom-package-tab.component.html',
  styles: ``,
})
export class CustomPackageTabComponent {
  // Local variables
  today: Date;
  peopleOptions: any[] = [
    { label: '1', value: 1 },
    { label: '2', value: 2 },
    { label: '3', value: 3 },
    { label: '4', value: 4 },
    { label: '5', value: 5 },
  ];
  packageForm!: FormGroup;

  // State variables
  @Input() params?: CustomPackageQueryParams;
  @Input() disableAgencyPackageView = false;

  // Destination search suggestions
  private fromSuggestions$ = new Subject<string>();
  private toSuggestions$ = new Subject<string>();
  fromSuggestions: { name: string; type: string }[] = [];
  toSuggestions: { name: string; type: string }[] = [];

  // Injectables
  private suggestionService = inject(SuggestionService);

  // Events
  @Output() onSearch = new EventEmitter<CustomPackageQueryParams>();
  @Output() onAgencyPackage = new EventEmitter();

  get f() {
    return this.packageForm.controls;
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
    this.packageForm = new FormGroup(
      {
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
        dates: new FormControl<Date[] | null>(
          this.params?.startDate && this.params?.endDate
            ? [new Date(this.params.startDate), new Date(this.params.endDate)]
            : null,
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(2),
          ],
        ),
        quantity: new FormControl(
          this.params?.quantity
            ? { label: `${this.params.quantity}`, value: this.params.quantity }
            : null,
          [Validators.required, Validators.min(1), Validators.max(5)],
        ),
        selectedOptions: new FormControl<string[]>(
          this.params?.options ?? ['flight', 'stay', 'activity'],
          Validators.required,
        ),
      },
      {
        validators: this.twoOptionsRequiredValidator,
      },
    );
  }

  onSearchFrom(event: AutoCompleteCompleteEvent) {
    this.fromSuggestions$.next(event.query);
  }

  onSearchTo(event: AutoCompleteCompleteEvent) {
    this.toSuggestions$.next(event.query);
  }

  handleAgencyPackage() {
    this.onAgencyPackage.emit();
  }

  isFromInvalid(): boolean {
    const fromValue = this.packageForm.getRawValue().from;
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

  isFormInvalid(): boolean {
    return (
      this.packageForm.invalid || this.isFromInvalid() || this.isToInvalid()
    );
  }

  submit() {
    // Validate form
    if (this.isFormInvalid()) return;

    const formValues = this.packageForm.getRawValue();

    // Send form data as query params
    const queryParams: CustomPackageQueryParams = {
      from: formValues.from.name,
      fromType: formValues.from.type,
      to: formValues.to.name,
      toType: formValues.to.type,
      startDate: formValues.dates[0].toISOString(),
      endDate: formValues.dates[1].toISOString(),
      quantity: formValues.quantity.value,
      options: formValues.selectedOptions.filter((v: string) => !!v),
    };
    this.onSearch.emit(queryParams);
  }

  /**
   * PRIVATE METHODS
   */

  private twoOptionsRequiredValidator(
    group: AbstractControl,
  ): ValidationErrors | null {
    const selected = group.get('selectedOptions')?.value;
    return Array.isArray(selected) && selected.length >= 2
      ? null
      : { twoOptionsRequired: true };
  }
}
