import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { SelectButtonModule } from 'primeng/selectbutton';
import { SliderModule } from 'primeng/slider';
import { DialogContainerComponent } from '../../../../../shared/dialog-container.component';
import { UserRole } from '../../../../../interfaces/model/User';
import { UserFilter } from '../../../../../interfaces/filters/UserFilter';

@Component({
  selector: 'smt-users-filter',
  standalone: true,
  imports: [
    CommonModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    SliderModule,
    FormsModule,
    SelectButtonModule,
    DialogContainerComponent,
  ],
  templateUrl: './users-filter.component.html',
  styles: ``,
})
export class UsersFilterComponent {
  // Local variables
  minPrice: number = 100;
  maxPrice: number = 15000;
  roleOptions = Object.values(UserRole).map((status) => ({
    label: status,
    value: status,
  }));

  // Status variables
  @Input({ required: true }) isVisible!: boolean;
  id?: string;
  email?: string;
  role?: string;

  // Events
  @Output() onClose = new EventEmitter();
  @Output() onFilter = new EventEmitter<UserFilter>();

  handleFilter() {
    const filters: UserFilter = {
      id: this.id ? this.id : undefined,
      email: this.email ? this.email : undefined,
      role: this.role ? this.role : undefined,
    };
    this.onFilter.emit(filters);
    this.onClose.emit();
  }

  handleReset() {
    this.id = undefined;
    this.email = undefined;
    this.role = undefined;
  }

  handleClose() {
    this.onClose.emit();
  }
}
