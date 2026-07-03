import {Component, inject} from "@angular/core";
import {MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {TourListItem} from '../../components/tour-list-item/tour-list-item';
import {Layout} from '../../layout/layout';
import {TourService} from '../../service/tour-service';
// import {Map} from '../../components/map/map';
// import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
// import {MatInput} from '@angular/material/input';

@Component({
  selector: 'app-my-tours',
  standalone: true,
  templateUrl: './my-tours.html',
  styleUrls: ['./my-tours.css'],
  imports: [
    MatIcon,
    MatFabButton,
    // MatFormField,
    // MatInput,
    // MatLabel,
    // MatSuffix,
    TourListItem,
    Layout,
    // Map
  ]
})
export class MyToursComponent {
  private readonly tourService = inject(TourService);

  readonly tours = this.tourService.tours;

  constructor() {
    this.tourService.loadTours();
    this.tourService.loadLogs();
  }
}
