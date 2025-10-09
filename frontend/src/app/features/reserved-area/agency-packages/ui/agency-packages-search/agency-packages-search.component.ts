import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DialogContainerComponent } from '../../../../../shared/dialog-container.component';
import { AgencyPackagesNewTabComponent } from '../../../agency-packages-new/ui/agency-packages-new-tab/agency-packages-new-tab.component';
import { AgencyPackageNewQueryParams } from '../../../../../interfaces/params/AgencyPackageNewQueryParams';

@Component({
  selector: 'smt-agency-packages-search',
  standalone: true,
  imports: [DialogContainerComponent, AgencyPackagesNewTabComponent],
  templateUrl: './agency-packages-search.component.html',
  styles: ``,
})
export class AgencyPackagesSearchComponent {
  // Status variables
  @Input({ required: true }) isVisible!: boolean;

  // Events
  @Output() onClose = new EventEmitter();
  @Output() onSearch = new EventEmitter<AgencyPackageNewQueryParams>();

  handleClose() {
    this.onClose.emit();
  }

  handleSearch(params: AgencyPackageNewQueryParams) {
    this.onSearch.emit(params);
  }
}
