import {Component, Input} from '@angular/core';
import {TourLog} from '../../models/tour-log';

@Component({
  selector: 'app-tour-log-card',
  standalone: true,
  imports: [],
  templateUrl: './tour-log-card.html',
  styleUrl: './tour-log-card.css',
})

export class TourLogCard {
  @Input({ required: true }) log!: TourLog;
}
