import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import {
  SelectButtonModule,
  SelectButtonChangeEvent,
} from 'primeng/selectbutton';
import { ToolbarModule } from 'primeng/toolbar';

@Component({
  selector: 'smt-orders-toolbar',
  standalone: true,
  imports: [
    CommonModule,
    ToolbarModule,
    InputTextModule,
    ButtonModule,
    SelectButtonModule,
    FormsModule,
  ],
  templateUrl: './orders-toolbar.component.html',
  styles: `
    ::ng-deep .toolbar-container .p-element {
      flex-grow: 1;
    }

    ::ng-deep .toolbar-container .p-toolbar-group-center {
      flex-grow: 1;
      justify-content: center;
    }
  `,
})
export class OrdersToolbarComponent {
  // State variables
  @Input({ required: true }) filterCounter!: number;
  query: string = '';

  // Events
  @Output() onDisplayFilters = new EventEmitter();
  @Output() onSearch = new EventEmitter<string>();

  handleDisplayFilters() {
    this.onDisplayFilters.emit();
  }

  handleSearch(value: string) {
    this.onSearch.emit(value);
  }
}
