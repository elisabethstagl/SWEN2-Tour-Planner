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
  @Input({ required: true }) tour!: Tour;
  @Input({ required: true }) popularity!: number;
}
