import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { AgencyPackage } from '../../../../../interfaces/model/AgencyPackage';
import { PackageStatus } from '../../../../../interfaces/enums/PackageStatus';

@Component({
  selector: 'smt-agency-packages-actions',
  standalone: true,
  imports: [CommonModule, MenuModule, ButtonModule],
  templateUrl: './agency-packages-actions.component.html',
  styles: ``,
})
export class AgencyPackagesActionsComponent {
  // Local variables
  items: MenuItem[] | undefined;

  // State variables
  @Input({ required: true }) agencyPackage!: AgencyPackage;

  // Events
  @Output() onViewDetails = new EventEmitter<string>();
  @Output() onPublishPackage = new EventEmitter<string>();
  @Output() onArchivePackage = new EventEmitter<string>();
  @Output() onDeletePackage = new EventEmitter<string>();

  ngOnInit() {
    this.items = [
      {
        label: 'View details',
        icon: 'pageview',
        callback: () => this.handleViewDetails(),
      },
    ];
    // Check package status
    switch (this.agencyPackage.status) {
      case PackageStatus.ARCHIVED:
        // Unarchive option available only if package startDate < current date
        // if (this.agencyPackage.startDate > new Date().toISOString()) {
        //   this.items.push({
        //     label: 'Unarchive package',
        //     icon: 'unarchive',
        //     callback: () => this.handlePublishPackage(),
        //   });
        // }
        break;
      case PackageStatus.DRAFT:
        // Publish and delete package options
        this.items.push({
          label: 'Publish package',
          icon: 'publish',
          callback: () => this.handlePublishPackage(),
        });
        this.items.push({
          label: 'Delete package',
          icon: 'delete',
          callback: () => this.handleDeletePackage(),
        });
        break;
      case PackageStatus.PUBLISHED:
        // Archive package option
        this.items.push({
          label: 'Archive package',
          icon: 'archive',
          callback: () => this.handleArchivePackage(),
        });
    }
  }

  handleViewDetails() {
    this.onViewDetails.emit(this.agencyPackage.id);
  }

  handlePublishPackage() {
    this.onPublishPackage.emit(this.agencyPackage.id);
  }

  handleArchivePackage() {
    this.onArchivePackage.emit(this.agencyPackage.id);
  }

  handleDeletePackage() {
    this.onDeletePackage.emit(this.agencyPackage.id);
  }
}
