import {Component, computed, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {ActivatedRoute, Router, RouterLink, RouterLinkActive} from "@angular/router";
import {TourService} from '../../tour-service';
import {toSignal} from '@angular/core/rxjs-interop';
import {map} from 'rxjs';
import {Layout} from '../../layout/layout';
import {TourDetailsCard} from '../../components/tour-details-card/tour-details-card';
import {TourLogCard} from '../../components/tour-log-card/tour-log-card';
import {EditDeleteButtons} from '../../components/edit-delete-buttons/edit-delete-buttons';

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
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tourService = inject(TourService);

  readonly tourId = toSignal(
    this.route.paramMap.pipe(
      map(params => Number(params.get('id')))
    ),
    { initialValue: 0 }
  );

  readonly tour = computed(() => {
    const id = this.tourId();
    return this.tourService.tours().find(t => t.id === id) ?? null;
  });

  readonly logs = computed(() => {
    const id = this.tourId();
    return this.tourService.logs().filter(log => log.tourId === id);
  });

  readonly popularity = computed(() => this.logs().length);

  editTour(): void {
    this.router.navigate(['/edit-tour', this.tourId()]);
  }

  deleteTour(): void {
    this.tourService.deleteTour(this.tourId());
    this.router.navigate(['/tours']);
  }

}
