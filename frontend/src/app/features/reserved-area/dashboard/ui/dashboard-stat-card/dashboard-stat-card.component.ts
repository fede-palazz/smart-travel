import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'smt-dashboard-stat-card',
  standalone: true,
  imports: [CommonModule, CardModule],
  templateUrl: './dashboard-stat-card.component.html',
  styles: `
    .stat-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
    }

    .stat-icon {
      font-size: 2.5rem;
      color: #007ad9;
      margin-block: 0.5rem;
    }

    .stat-number {
      font-size: 1.8rem;
      font-weight: 500;
    }
  `,
})
export class DashboardStatCardComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) icon!: string;
  @Input({ required: true }) value!: string;
}
