import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TextComponent } from '../../../../../shared/text.component';
import { CardModule } from 'primeng/card';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { urlValidator } from '../../../../../utils/validators/urlValidator';
import { ButtonModule } from 'primeng/button';
import { MultiSelectModule } from 'primeng/multiselect';
import { PartialAgencyPackage } from '../../../../../interfaces/model/AgencyPackage';

@Component({
  selector: 'smt-agency-packages-confirmation',
  standalone: true,
  imports: [
    CommonModule,
    TextComponent,
    CardModule,
    ReactiveFormsModule,
    InputTextModule,
    InputNumberModule,
    InputTextareaModule,
    InputNumberModule,
    ButtonModule,
    MultiSelectModule,
  ],
  templateUrl: './agency-packages-confirmation.component.html',
  styles: `
    :host ::ng-deep .checkout-container .p-card .p-card-body {
      padding-inline: 2rem;
    }
  `,
})
export class AgencyPackagesConfirmationComponent {
  // Local variables
  packageForm!: FormGroup;
  tagsOptions = [
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

  // Status variables
  @Input() isSaving = false;

  // Events
  @Output() onSave = new EventEmitter<PartialAgencyPackage>();

  get f() {
    return this.packageForm.controls;
  }

  ngOnInit() {
    // Define form group
    this.packageForm = new FormGroup({
      name: new FormControl<string | null>(null, Validators.required),
      tags: new FormControl<{ name: string; value: string }[] | null>(null, [
        Validators.required,
        Validators.minLength(1),
      ]),
      description: new FormControl<string | null>(null, Validators.required),
      totalPrice: new FormControl<number | null>(null, Validators.required),
      mainPicture: new FormControl<string | null>(null, [
        Validators.required,
        urlValidator(),
      ]),
    });
  }

  isFormInvalid(): boolean {
    return this.packageForm.invalid;
  }

  handleSave() {
    // Validate form
    if (this.isFormInvalid()) return;

    const formValues = this.packageForm.getRawValue();

    // Send form data as query params
    const values: PartialAgencyPackage = {
      name: formValues.name,
      tags: formValues.tags,
      description: formValues.description,
      totalPrice: { value: formValues.totalPrice, currency: 'EUR' },
      mainPicture: formValues.mainPicture,
    };
    this.onSave.emit(values);
  }
}
