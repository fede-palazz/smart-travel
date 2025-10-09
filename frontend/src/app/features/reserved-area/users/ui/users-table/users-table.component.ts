import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { User, UserRole } from '../../../../../interfaces/model/User';
import { UsersActionsComponent } from '../users-actions/users-actions.component';

@Component({
  selector: 'smt-users-table',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    MenuModule,
    RippleModule,
    ButtonModule,
    PaginatorModule,
    UsersActionsComponent,
  ],
  templateUrl: './users-table.component.html',
  styles: `
    ::ng-deep .p-datatable-footer {
      padding: 0;
    }
  `,
})
export class UsersTableComponent {
  // Status variables
  @Input({ required: true }) users!: User[];
  @Input({ required: true }) pageSize!: number;
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalElements!: number;

  // Events
  @Output() onPageChange = new EventEmitter<number>();
  @Output() onDeleteUser = new EventEmitter<string>();

  getSeverity(role: UserRole) {
    switch (role) {
      case UserRole.CUSTOMER:
        return 'success';

      case UserRole.AGENT:
        return 'secondary';

      case UserRole.ADMIN:
        return 'info';

      default:
        return 'danger';
    }
  }

  handlePageChange(event: PaginatorState) {
    this.onPageChange.emit(event.page ?? 0);
  }

  handleDeleteUser(id: string) {
    this.onDeleteUser.emit(id);
  }
}
