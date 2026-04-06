import {Component, inject, Input} from '@angular/core';
import {Router} from '@angular/router';
import {Tour} from '../../models/tour';

@Component({
  selector: 'app-tour-list-item',
  standalone: true,
  imports: [],
  templateUrl: './tour-list-item.html',
  styleUrl: './tour-list-item.css',
})

export class TourListItem {
  @Input({ required: true }) tour!: Tour;

  private readonly router = inject(Router);

  openTour() {
    this.router.navigate(['/tour-detail', this.tour.id]);
  }
}
