import {Component, inject, Input} from '@angular/core';
import {Router} from '@angular/router';
import {Tour} from '../../models/tour';
import {MatChipsModule} from '@angular/material/chips';
import {MatIcon} from '@angular/material/icon';
import {NgClass} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-tour-list-item',
  standalone: true,
  imports: [MatChipsModule, MatIcon, NgClass, MatButtonModule],
  templateUrl: './tour-list-item.html',
  styleUrl: './tour-list-item.css',
})

export class TourListItem {
  @Input({required: true}) tour!: Tour;

  private readonly router = inject(Router);

  openTour() {
    this.router.navigate(['/tour-detail', this.tour.id]);
  }

  // getTransportLabel(type: string): string {
  //   switch (type) {
  //     case 'bike':
  //       return 'Bike';
  //     case 'walk':
  //       return 'Walk';
  //     case 'hike':
  //       return 'Hike';
  //     case 'car':
  //       return 'Car';
  //     case 'wheelchair':
  //       return 'Wheelchair';
  //     default:
  //       return type;
  //   }
  // }

  getTransportIcon(type: string): string {
    switch (type) {
      case 'bike':
        return 'directions_bike';
      case 'walk':
        return 'directions_walk';
      case 'hike':
        return 'hiking';
      case 'car':
        return 'directions_car';
      case 'wheelchair':
        return 'accessible';
      default:
        return 'place';
    }
  }
}
