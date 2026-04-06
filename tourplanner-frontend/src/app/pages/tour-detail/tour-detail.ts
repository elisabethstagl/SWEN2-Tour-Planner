import {Component} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {RouterLink, RouterLinkActive} from "@angular/router";

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './tour-detail.html',
  styleUrl: './tour-detail.css',
})
export class TourDetail {

}
