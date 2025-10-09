import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccordionModule } from 'primeng/accordion';
import { Activity } from '../../interfaces/model/Activity';
import { ParamMap, Router, ActivatedRoute } from '@angular/router';
import { map, Observable } from 'rxjs';
import { ActivityFilter } from '../../interfaces/filters/ActivityFilter';
import { PaginationFilter } from '../../interfaces/filters/PaginationFilter';
import { ActivityQueryParams } from '../../interfaces/params/ActivityQueryParams';
import { CheckoutService } from '../checkout/services/checkout.service';
import { RouterUtils } from '../../utils/RouterUtils';
import { ActivityTabComponent } from '../home-tabs/ui/activity-tab/activity-tab.component';
import { ActivityViewComponent } from './ui/activity-view/activity-view.component';

@Component({
  selector: 'smt-activity',
  standalone: true,
  imports: [
    CommonModule,
    AccordionModule,
    ActivityTabComponent,
    ActivityViewComponent,
  ],
  templateUrl: './activity.component.html',
  styles: ``,
})
export class ActivityComponent {
  // Local variables
  activityParams$!: Observable<{
    searchParams: ActivityQueryParams;
    paginationParams: PaginationFilter;
    filterParams: ActivityFilter;
  }>;

  // Injectables
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private checkoutService = inject(CheckoutService);

  ngOnInit() {
    // Reactively listen to query param changes
    this.activityParams$ = this.route.queryParamMap.pipe(
      map((params) => ({
        searchParams: this.extractSearchParams(params),
        paginationParams: this.extractPaginationParams(params),
        filterParams: this.extractFilterParams(params),
      })),
    );
  }

  handleActivitySearch(queryParams: ActivityQueryParams) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
    });
  }

  handleSelectActivity(activity: Activity) {
    const startDate = this.route.snapshot.queryParamMap.get('startDate') ?? '';
    const endDate = this.route.snapshot.queryParamMap.get('endDate') ?? '';

    // Set return url for adding a new activity
    this.checkoutService.setReturnUrl(
      RouterUtils.getCleanedUrl(this.router, this.router.url),
    );
    this.router.navigate(['/activities', activity.id], {
      queryParams: {
        startDate,
        endDate,
      },
    });
  }

  /**
   * PRIVATE METHODS
   */

  private extractSearchParams(params: ParamMap): ActivityQueryParams {
    return {
      to: params.get('to') ?? '',
      toType: params.get('toType') ?? '',
      startDate: params.get('startDate') ?? '',
      endDate: params.get('endDate') ?? '',
    };
  }

  private extractPaginationParams(params: ParamMap): PaginationFilter {
    return {
      page: Number(params.get('page')) || 0,
      size: Number(params.get('size')) || 5,
      sort: params.get('sort') ?? undefined,
      order: params.get('order') ?? undefined,
    };
  }

  private extractFilterParams(params: ParamMap): ActivityFilter {
    return {
      name: params.get('name') ?? undefined,
      types: params.getAll('types') ?? [],
      tags: params.getAll('tags') ?? [],
      languages: params.getAll('languages') ?? [],
      minPrice: Number(params.get('minPrice')) || undefined,
      maxPrice: Number(params.get('maxPrice')) || undefined,
      minRating: Number(params.get('minRating')) || undefined,
    };
  }
}
