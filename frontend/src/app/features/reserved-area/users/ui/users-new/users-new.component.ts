import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UserReq, UserRole } from '../../../../../interfaces/model/User';
import { DialogContainerComponent } from '../../../../../shared/dialog-container.component';
import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { SelectButtonModule } from 'primeng/selectbutton';

@Component({
  selector: 'smt-users-new',
  standalone: true,
  imports: [
    DialogContainerComponent,
    ButtonModule,
    ReactiveFormsModule,
    InputTextModule,
    CommonModule,
    SelectButtonModule,
  ],
  templateUrl: './users-new.component.html',
  styles: ``,
})
export class UsersNewComponent {
  // Local variables
  userForm!: FormGroup;
  roleOptions = Object.values(UserRole).map((status) => ({
    label: status,
    value: status,
  }));

  // Status variables
  @Input({ required: true }) isVisible!: boolean;
  @Input({ required: true }) isLoading!: boolean;

  get f() {
    return this.userForm.controls;
  }

  ngOnInit() {
    // Initialize form group
    this.userForm = new FormGroup({
      firstname: new FormControl('', [Validators.required]),
      lastname: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(25),
      ]),
      role: new FormControl('', Validators.required),
    });
  }

  // Events
  @Output() onClose = new EventEmitter();
  @Output() onSubmit = new EventEmitter<UserReq>();

  handleClose() {
    this.onClose.emit();
  }

  handleSubmit() {
    if (this.userForm.invalid) {
      return;
    }
    const values = this.userForm.getRawValue();
    const request: UserReq = {
      firstname: values.firstname,
      lastname: values.lastname,
      email: values.email,
      password: values.password,
      role: values.role,
    };
    this.onSubmit.emit(request);
  }
}
