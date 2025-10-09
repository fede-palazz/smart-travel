import { Component, inject } from '@angular/core';
import { UserFilter } from '../../../interfaces/filters/UserFilter';
import { User, UserReq } from '../../../interfaces/model/User';
import {
  BehaviorSubject,
  debounceTime,
  map,
  Observable,
  switchMap,
} from 'rxjs';
import { PaginationFilter } from '../../../interfaces/filters/PaginationFilter';
import { QueryResult } from '../../../interfaces/QueryResult';
import { PagedRes } from '../../../interfaces/PagedRes';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { NotFoundComponent } from '../../../shared/not-found.component';
import { DataViewModule } from 'primeng/dataview';
import { AgencyPackagesTableSkeletonComponent } from '../agency-packages/ui/agency-packages-table-skeleton/agency-packages-table-skeleton.component';
import { ButtonModule } from 'primeng/button';
import { UserService } from './services/user.service';
import { UsersToolbarComponent } from './ui/users-toolbar/users-toolbar.component';
import { UsersTableComponent } from './ui/users-table/users-table.component';
import { UsersFilterComponent } from './ui/users-filter/users-filter.component';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { UsersNewComponent } from './ui/users-new/users-new.component';

@Component({
  selector: 'smt-users',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    CardModule,
    NotFoundComponent,
    DataViewModule,
    AgencyPackagesTableSkeletonComponent,
    UsersToolbarComponent,
    UsersTableComponent,
    UsersFilterComponent,
    ToastModule,
    UsersNewComponent,
  ],
  templateUrl: './users.component.html',
  styles: `
    ::ng-deep .p-dataview .p-dataview-header {
      background-color: inherit;
      border: 0;
      padding-inline: 8px;
    }

    ::ng-deep .p-dataview .p-dataview-emptymessage {
      display: none;
    }
  `,
})
export class UsersComponent {
  // Status variables
  displayFilters = false; // Filters popup
  displayNewUserDialog = false; // Add user popup
  addUserLoading = false; // Add user loading spinner

  pageSize = 5;

  filterSubject = new BehaviorSubject<{
    filterParams: UserFilter;
    paginationParams: PaginationFilter;
  }>({ filterParams: {}, paginationParams: { size: this.pageSize } });
  filters$!: Observable<{
    filterParams: UserFilter;
    paginationParams: PaginationFilter;
  }>;
  filterCounter$!: Observable<number>;
  users$!: Observable<QueryResult<PagedRes<User>>>;

  // Injectables
  private userService = inject(UserService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.filters$ = this.filterSubject.asObservable();
    this.filterCounter$ = this.filters$.pipe(
      map(({ filterParams }) => this.getFilterCounter(filterParams)),
    );
    this.users$ = this.filters$.pipe(
      debounceTime(300), // wait 300ms after last emission
      switchMap(({ filterParams, paginationParams }) =>
        this.fetchUsers(filterParams, paginationParams),
      ),
    );
  }

  handleFilter(filters: UserFilter) {
    const currentFilters = this.filterSubject.getValue().filterParams;
    const name = currentFilters.name;

    // Explicitly handle undefined values by removing them
    Object.keys(filters).forEach((key) => {
      const typedKey = key as keyof UserFilter;
      const newFilter = filters[typedKey];

      if (newFilter === undefined || newFilter === null || newFilter === '') {
        delete currentFilters[typedKey];
      } else {
        currentFilters[typedKey] = newFilter as any;
      }
    });

    // Update filters and remove pagination filter
    this.filterSubject.next({
      filterParams: {
        ...currentFilters,
        ...(name !== undefined ? { name: name } : {}), // Add search bar value only if defined
      },
      paginationParams: { size: this.pageSize },
    });
  }

  handleSearch(query: string) {
    const currentFilters = this.filterSubject.getValue().filterParams;

    if (!query) {
      delete currentFilters['name'];
    }

    this.filterSubject.next({
      filterParams: {
        ...currentFilters,
        ...(query ? { name: query } : {}), // Add search bar value only if defined
      },
      paginationParams: { size: this.pageSize },
    });
  }

  handlePageChange(page: number): void {
    const currentFilters = this.filterSubject.getValue();
    this.filterSubject.next({
      filterParams: {
        ...currentFilters.filterParams,
      },
      paginationParams: { page, size: currentFilters.paginationParams.size },
    });
  }

  handleAddUser(userReq: UserReq) {
    this.addUserLoading = true;
    const currentFilters = this.filterSubject.getValue();
    this.userService
      .addUser(
        userReq,
        currentFilters.filterParams,
        currentFilters.paginationParams,
      )
      .subscribe(({ data, error }) => {
        this.addUserLoading = false;
        if (error) {
          console.error(error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error while performing the request',
            detail: error,
          });
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: 'User Created',
            detail: 'The user has been successfully created',
          });
          this.displayNewUserDialog = false;
        }
      });
  }

  handleDeleteUser(id: string) {
    const currentFilters = this.filterSubject.getValue();
    this.userService
      .deleteUser(
        id,
        currentFilters.filterParams,
        currentFilters.paginationParams,
      )
      .subscribe(({ data, error }) => {
        if (error) {
          console.error(error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error while performing the request',
            detail: error,
          });
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: 'User Deleted',
            detail: 'The user has been successfully deleted',
          });
        }
      });
  }

  /**
   * PRIVATE METHODS
   */

  private fetchUsers(
    filters?: UserFilter,
    pagination?: PaginationFilter,
  ): Observable<QueryResult<PagedRes<User>>> {
    return this.userService.getUsers(filters, pagination);
  }

  private getFilterCounter(filterParams: UserFilter): number {
    let counter = 0;

    if (filterParams.id) counter++;
    if (filterParams.name) counter++;
    if (filterParams.email) counter++;
    if (filterParams.role) counter++;

    return counter;
  }
}
