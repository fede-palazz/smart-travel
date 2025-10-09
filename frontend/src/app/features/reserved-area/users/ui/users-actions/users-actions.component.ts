import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { User } from '../../../../../interfaces/model/User';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';

@Component({
  selector: 'smt-users-actions',
  standalone: true,
  imports: [CommonModule, MenuModule, ButtonModule],
  templateUrl: './users-actions.component.html',
  styles: ``,
})
export class UsersActionsComponent {
  // Local variables
  items: MenuItem[] | undefined;

  // State variables
  @Input({ required: true }) user!: User;

  // Events
  @Output() onDeleteUser = new EventEmitter<string>();

  ngOnInit() {
    this.items = [
      {
        label: 'Delete user',
        icon: 'delete',
        callback: () => this.handleDeleteUser(),
      },
    ];
  }

  handleDeleteUser() {
    this.onDeleteUser.emit(this.user.id);
  }
}
