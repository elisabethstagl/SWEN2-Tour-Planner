import {Component, inject} from "@angular/core";
import {MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {TourListItem} from '../../components/tour-list-item/tour-list-item';
import {Layout} from '../../layout/layout';
import {TourService} from '../../tour-service';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  imports: [
    MatIcon,
    MatFabButton,
    TourListItem,
    Layout
  ]
})
export class HomeComponent {
  private readonly tourService = inject(TourService)

  readonly tours = this.tourService.tours;
}
