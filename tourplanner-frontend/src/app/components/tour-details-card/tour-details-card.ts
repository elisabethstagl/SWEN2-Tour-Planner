import {Component, Input} from '@angular/core';
import {Tour} from '../../models/tour';

@Component({
  selector: 'app-tour-details-card',
  standalone: true,
  imports: [],
  templateUrl: './tour-details-card.html',
  styleUrl: './tour-details-card.css',
})

export class TourDetailsCard {
  @Input({required: true}) tour!: Tour;
  @Input({required: true}) popularity!: number;

  formatEstimatedTime(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (hours === 0) {
      return `${remainingMinutes} min`;
    }

    if (remainingMinutes === 0) {
      return `${hours} h`;
    }

    return `${hours} h ${remainingMinutes} min`;
  }

  formatDistance(distance: number): string {
    return `${distance.toFixed(2)} km`;
  }
}
