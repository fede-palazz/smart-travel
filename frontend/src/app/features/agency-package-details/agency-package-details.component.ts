import { Component, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CarouselModule } from 'primeng/carousel';
import { ChipModule } from 'primeng/chip';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { AgencyPackageDetailsService } from './services/agency-package-details.service';
import { ActivatedRoute, Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { AgencyPackage } from '../../interfaces/model/AgencyPackage';
import { QueryResult } from '../../interfaces/QueryResult';
import { AgencyPackageDetailsCardComponent } from './ui/agency-package-details-card/agency-package-details-card.component';
import { AuthService } from '../../services/auth.service';
import { QuickActionsBarComponent } from '../../shared/quick-actions-bar.component';
import { NotFoundComponent } from '../../shared/not-found.component';
import { User, UserRole } from '../../interfaces/model/User';

@Component({
  selector: 'smt-agency-package-details',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    TableModule,
    ChipModule,
    CarouselModule,
    ButtonModule,
    ToastModule,
    DropdownModule,
    InputNumberModule,
    ReactiveFormsModule,
    TagModule,
    AgencyPackageDetailsCardComponent,
    QuickActionsBarComponent,
    NotFoundComponent,
  ],
  templateUrl: './agency-package-details.component.html',
  styles: ``,
})
export class AgencyPackageDetailsComponent {
  // Local variables
  role = UserRole;

  // Status variables
  agencyPackageDetails$!: Observable<QueryResult<AgencyPackage>>;
  isAgentView$!: Observable<boolean | null>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private location = inject(Location);
  private authService = inject(AuthService);
  private agencyPackageDetailsService = inject(AgencyPackageDetailsService);

  ngOnInit() {
    // Fetch accommodation data
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id.length === 24) {
      this.agencyPackageDetails$ = this.fetchAgencyPackageDetails(id);
    } else this.router.navigate(['/home']);
    // Fetch user
    this.isAgentView$ = this.authService.user$.pipe(
      map(
        (user) => user && [UserRole.AGENT, UserRole.ADMIN].includes(user.role),
      ),
    );
  }

  handleNavigateBack(): void {
    if (window.history.length > 1) {
      this.location.back();
      return;
    } else this.router.navigate(['/home/agency-packages']);
  }

  handleCheckout(agencyPackageId: string) {
    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      const returnUrl = this.router.url;
      this.router.navigate(['/login'], {
        queryParams: { returnUrl },
      });
      return;
    }
    // Perform checkout
    this.agencyPackageDetailsService
      .performCheckout(agencyPackageId)
      .subscribe(({ data, error }) => {
        if (error) {
          console.error(error);
          return;
        }
        if (data) window.location.href = data.redirectURL;
      });
  }

  /**
   * PRIVATE METHODS
   */

  private fetchAgencyPackageDetails(
    id: string,
  ): Observable<QueryResult<AgencyPackage>> {
    return this.agencyPackageDetailsService.getAgencyPackageDetails(id);
  }
}
