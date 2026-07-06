import {Component, computed, inject, signal} from "@angular/core";
import {MatIcon} from '@angular/material/icon';
import {TourListItem} from '../../components/tour-list-item/tour-list-item';
import {Layout} from '../../layout/layout';
import {TourService} from '../../service/tour-service';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {TransportType} from '../../models/tour';
import {MatIconButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-my-tours',
  standalone: true,
  templateUrl: './my-tours.html',
  styleUrls: ['./my-tours.css'],
  imports: [
    MatIcon,
    TourListItem,
    Layout,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatIconButton
  ]
})
export class MyToursComponent {
  private readonly tourService = inject(TourService);
  private readonly snackBar = inject(MatSnackBar);

  readonly tours = this.tourService.tours;
  readonly error = this.tourService.error;
  readonly selectedTransportType = signal<TransportType | 'all'>('all');

  readonly transportTypes: TransportType[] = [
    'bike',
    'hike',
    'walk',
    'wheelchair',
    'car'
  ];

  readonly filteredTours = computed(() => {
    const filter = this.selectedTransportType();

    if (filter === 'all') {
      return this.tours();
    }

    return this.tours().filter(tour => tour.transportType === filter);
  });

  constructor() {
    this.tourService.loadTours();
    this.tourService.loadLogs();
  }

  setTransportFilter(type: TransportType | 'all'): void {
    this.selectedTransportType.set(type);
  }

  onExport(): void {
    this.tourService.exportTours();
  }

  onImportFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    this.tourService.importTours(file, importedCount => {
      const tourWord = importedCount === 1 ? 'tour' : 'tours';

      this.snackBar.open(
        `${importedCount} ${tourWord} imported successfully!`,
        '',
        {
          duration: 2000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['success-snackbar']
        }
      );
    });

    // reset so selecting the same file again still triggers a change event
    input.value = '';
  }

  getTransportLabel(type: TransportType | 'all'): string {
    switch (type) {
      case 'all':
        return 'All';
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

  isSelected(type: TransportType | 'all'): boolean {
    return this.selectedTransportType() === type;
  }
}
