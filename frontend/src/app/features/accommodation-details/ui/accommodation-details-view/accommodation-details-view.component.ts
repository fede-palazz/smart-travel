import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  FormArray,
  FormControl,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CarouselModule } from 'primeng/carousel';
import { ChipModule } from 'primeng/chip';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { map, Observable, tap } from 'rxjs';
import { Accommodation } from '../../../../interfaces/model/Accommodation';
import { Room } from '../../../../interfaces/model/shared/Room';
import { AccommodationOrder } from '../../../../interfaces/orders/AccommodationOrder';
import { QueryResult } from '../../../../interfaces/QueryResult';
import { QuickActionsBarComponent } from '../../../../shared/quick-actions-bar.component';
import { AccommodationDetailsService } from '../../services/accommodation-details.service';
import { AccommodationDetailsCardComponent } from '../accommodation-details-card/accommodation-details-card.component';
import { NotFoundComponent } from '../../../../shared/not-found.component';
import { AuthService } from '../../../../services/auth.service';
import { UserRole } from '../../../../interfaces/model/User';

@Component({
  selector: 'smt-accommodation-details-view',
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
    AccommodationDetailsCardComponent,
    QuickActionsBarComponent,
    NotFoundComponent,
  ],
  templateUrl: './accommodation-details-view.component.html',
  styles: ``,
})
export class AccommodationDetailsViewComponent {
  // Local variables
  form!: FormGroup;

  // Status variables
  @Input() isAgentView: boolean = false;
  @Input({ required: true }) accommodationId!: string;
  @Input() previousUrl?: string;
  accommodationDetails$!: Observable<QueryResult<Accommodation>>;

  // Injectables
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private accommodationDetailsService = inject(AccommodationDetailsService);

  // Events
  @Output() onNavigateBack = new EventEmitter();
  @Output() onCheckout = new EventEmitter<AccommodationOrder>();

  ngOnInit() {
    // Initialize room quantities form
    this.form = this.fb.group({
      roomQuantities: this.fb.array([]),
    });
    // Fetch accommodation data
    this.accommodationDetails$ = this.fetchAccommodationDetails(
      this.accommodationId,
    );
  }

  get roomQuantities(): FormArray {
    return this.form.get('roomQuantities') as FormArray;
  }

  handleNavigateBack() {
    this.onNavigateBack.emit();
  }

  handleCheckout(accommodationDetails: Accommodation) {
    const selectedRooms = this.getSelectedRooms(
      accommodationDetails,
      this.roomQuantities,
    );
    const startDate = this.route.snapshot.queryParamMap.get('startDate');
    const endDate = this.route.snapshot.queryParamMap.get('endDate');

    if (!accommodationDetails || !startDate || !endDate) return;

    const accommodationOrder: AccommodationOrder = {
      accommodationId: accommodationDetails.id,
      name: accommodationDetails.name,
      type: accommodationDetails.type,
      mainPicture: accommodationDetails.mainPicture,
      rooms: selectedRooms,
      startDate: startDate,
      endDate: endDate,
    };
    this.onCheckout.emit(accommodationOrder);
  }

  /**
   * PRIVATE METHODS
   */

  private fetchAccommodationDetails(
    id: string,
  ): Observable<QueryResult<Accommodation>> {
    return this.accommodationDetailsService.getAccommodationDetails(id).pipe(
      tap((res) => {
        // Initialize room quantities
        if (res && res.data && res.data.rooms) {
          res.data.rooms.forEach((_) => {
            this.roomQuantities.push(new FormControl(0));
          });
        }
      }),
    );
  }

  private getSelectedRooms(
    accommodationDetails: Accommodation,
    quantityArray: FormArray,
  ): Room[] {
    const quantities = quantityArray.value;
    return accommodationDetails!.rooms
      .map((room, index) => ({
        ...room,
        quantity: quantities[index],
      }))
      .filter((room) => room.quantity > 0);
  }
}
