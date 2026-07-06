import {Component, computed, effect, inject, input, signal} from '@angular/core'; // input importieren
import {Router, RouterLink} from "@angular/router";
import {TourService} from '../../service/tour-service';
import {Layout} from '../../layout/layout';
import {TourDetailsCard} from '../../components/tour-details-card/tour-details-card';
import {TourLogCard} from '../../components/tour-log-card/tour-log-card';
import {EditDeleteButtons} from '../../components/edit-delete-buttons/edit-delete-buttons';
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {Map} from '../../components/map/map';
import {Tour, TransportType} from '../../models/tour';
import { RouteService } from '../../service/route-service';
import {MatIcon} from '@angular/material/icon';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    Layout,
    TourDetailsCard,
    TourLogCard,
    EditDeleteButtons,
    Map,
    MatIcon,
    MatMiniFabButton,
    NgClass
  ],
  templateUrl: './tour-detail.html',
  styleUrl: './tour-detail.css',
})
export class TourDetail {
  private readonly router = inject(Router);
  private readonly tourService = inject(TourService);
  private readonly routeService = inject(RouteService);

  constructor() {
    effect(() => {
      const currentTour = this.tour();

      if (currentTour) {
        this.loadRouteForTour(currentTour);
      }
    });
  }

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

  onEditTour(): void {
    this.router.navigate(['/edit-tour', this.id()]);
  }

  onDeleteTour(): void {
    this.tourService.deleteTour(this.id());
    this.router.navigate(['/my-tours']);
  }

  onEditLog(logId: number): void {
    this.router.navigate(['/tour', this.id(), 'log', 'edit', logId]);
  }

  onDeleteLog(logId: number): void {
    this.tourService.deleteLog(logId);
  }

  routePoints = signal<[number, number][] | null>(null);

  loadRouteForTour(tour: Tour): void {
    if (tour.routeGeometry && tour.routeGeometry.length > 0) {
      this.routePoints.set(tour.routeGeometry);
      return;
    }

    this.routeService.calculateRoute({
      from: tour.from,
      to: tour.to,
      transportType: tour.transportType
    }).subscribe({
      next: response => {
        this.routePoints.set(response.coordinates as [number, number][]);
      },
      error: () => {
        console.error('Could not load route');
      }
    });
  }

  getTransportLabel(type: TransportType): string {
    switch (type) {
      case 'bike':
        return 'Bike';
      case 'hike':
        return 'Hike';
      case 'walk':
        return 'Walk';
      case 'wheelchair':
        return 'Wheelchair';
      case 'car':
        return 'Car';
    }
  }

  getTransportIcon(type: TransportType): string {
    switch (type) {
      case 'bike':
        return 'directions_bike';
      case 'hike':
        return 'hiking';
      case 'walk':
        return 'directions_walk';
      case 'wheelchair':
        return 'accessible';
      case 'car':
        return 'directions_car';
    }
  }
}
