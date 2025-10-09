import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import {
  SelectButtonChangeEvent,
  SelectButtonModule,
} from 'primeng/selectbutton';
import { ToolbarModule } from 'primeng/toolbar';

@Component({
  selector: 'smt-agency-packages-toolbar',
  standalone: true,
  imports: [
    CommonModule,
    ToolbarModule,
    InputTextModule,
    ButtonModule,
    SelectButtonModule,
    FormsModule,
  ],
  templateUrl: './agency-packages-toolbar.component.html',
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
export class AgencyPackagesToolbarComponent {
  // Local variables
  layoutOptions: any[] = [
    { icon: 'pi pi-bars', layout: 'list' },
    { icon: 'pi pi-th-large', layout: 'grid' },
  ];

  // State variables
  @Input({ required: true }) layout!: string;
  @Input({ required: true }) filterCounter!: number;
  query: string = '';

  // Events
  @Output() onLayoutChange = new EventEmitter<'list' | 'grid'>();
  @Output() onDisplayFilters = new EventEmitter();
  @Output() onSearch = new EventEmitter<string>();

  handleLayoutChange(event: SelectButtonChangeEvent) {
    this.onLayoutChange.emit(event.value);
  }

  handleDisplayFilters() {
    this.onDisplayFilters.emit();
  }

  handleSearch(value: string) {
    this.onSearch.emit(value);
  }
}
