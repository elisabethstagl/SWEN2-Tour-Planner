import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';

export interface Tour {
  id: number;
  title: string;
  description: string;
  from: string;
  to: string;
  distance: number;
  duration: string;
}

@Component({
  selector: 'app-tour-list-item',
  standalone: true,
  imports: [],
  templateUrl: './tour-list-item.html',
  styleUrl: './tour-list-item.css',
})

export class TourListItem {
  @Input() tour!: Tour;

  constructor(private router: Router) {}

  openTour() {
    this.router.navigate(['/tour-detail', this.tour.id]);
  }
}
