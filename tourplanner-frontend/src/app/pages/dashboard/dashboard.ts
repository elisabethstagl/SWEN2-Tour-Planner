import {Component} from '@angular/core';
import {Map} from '../../components/map/map';
import {TourForm} from '../tour-form/tour-form';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    Map,
    TourForm
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent {
}
