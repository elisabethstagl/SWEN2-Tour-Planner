import {Component, computed, inject, input} from '@angular/core'; // input importieren
import {Router, RouterLink} from "@angular/router";
import {TourService} from '../../tour-service';
import {Layout} from '../../layout/layout';
import {TourDetailsCard} from '../../components/tour-details-card/tour-details-card';
import {TourLogCard} from '../../components/tour-log-card/tour-log-card';
import {EditDeleteButtons} from '../../components/edit-delete-buttons/edit-delete-buttons';
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    Layout,
    TourDetailsCard,
    TourLogCard,
    EditDeleteButtons
  ],
  templateUrl: './tour-detail.html',
  styleUrl: './tour-detail.css',
})
export class TourDetail {
  private readonly router = inject(Router);
  private readonly tourService = inject(TourService);

  readonly id = input.required<number, string | number>({
    transform: (value) => Number(value)
  });

  readonly tour = computed(() => {
    const tourId = this.id();
    return this.tourService.tours().find(t => t.id === tourId) ?? null;
  });

  readonly logs = computed(() => {
    const tourId = this.id();
    return this.tourService.logs().filter(log => log.tourId === tourId);
  });

  readonly popularity = computed(() => this.logs().length);

  editTour(): void {
    this.router.navigate(['/edit-tour', this.id()]);
  }

  deleteTour(): void {
    this.tourService.deleteTour(this.id());
    this.router.navigate(['/tours']);
  }
}
