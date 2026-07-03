import {ChangeDetectionStrategy, Component, computed, inject, signal, input, effect} from '@angular/core';
import {Layout} from "../../layout/layout";
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {TourService} from '../../service/tour-service';
import {RouteService} from '../../service/route-service';
import {MapFacadeService} from '../../service/map-facade-service';
import {Tour, TransportType} from '../../models/tour';
import {Router} from '@angular/router';
import {MatButton} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {NgTemplateOutlet} from '@angular/common';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Map} from '../../components/map/map';

@Component({
  selector: 'app-tour-form',
  standalone: true,
  imports: [
    Layout,
    MatFormField,
    MatFormFieldModule,
    MatLabel,
    MatInput,
    MatSelect,
    MatOption,
    MatIconModule,
    MatButton,
    FormsModule,
    NgTemplateOutlet,
    Map
  ],
  templateUrl: './tour-form.html',
  styleUrl: './tour-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})
export class TourForm {
  private readonly tourService = inject(TourService);
  private readonly router = inject(Router);
  private readonly routeService = inject(RouteService);
  private readonly mapFacadeService = inject(MapFacadeService);
  private readonly snackBar = inject(MatSnackBar);

  readonly embedded = input(false);

  readonly id = input<number, string | undefined>(undefined, {
    transform: (v) => (v === undefined ? undefined : Number(v))
  });

  readonly error = this.tourService.error;
  readonly transportTypes: TransportType[] = [
    'bike',
    'hike',
    'walk',
    'wheelchair',
    'car'
  ];

  readonly tourModel = signal<Tour>({
    id: 0,
    userId: 1,
    name: '',
    description: '',
    from: '',
    to: '',
    transportType: 'walk',
    distance: null as any,
    estimatedTime: null as any,
  });

  constructor() {
    effect(() => {
      const tourId = this.id();
      if (tourId) {
        const existingTour = this.tourService.getTourById(tourId)();
        if (existingTour) {
          this.tourModel.set({...existingTour});
        }
      }
    });
  }

  updateField<K extends keyof Tour>(field: K, value: any): void {
    let finalValue = value;

    if (field === 'distance') {
      finalValue = value === '' || value === null ? null : Number(value);
    }

    this.tourModel.update(prev => {
      const updated = {...prev, [field]: finalValue};

      if (field === 'from' || field === 'to' || field === 'transportType') {
        updated.distance = null as any;
        updated.estimatedTime = null as any;
      }

      return updated;
    });

    this.clearError();
  }

  readonly nameError = computed(() => {
    const v = this.tourModel().name.trim();
    if (v.length === 0) return 'Tour name is required.';
    if (v.length < 2) return 'Tour name must be at least 2 characters.';
    return null;
  });

  readonly descriptionError = computed(() => {
    const v = this.tourModel().description.trim();
    if (v.length === 0) return 'Description is required.';
    if (v.length < 2) return 'Description must be at least 2 characters.';
    return null;
  });

  readonly fromError = computed(() => {
    const v = this.tourModel().from.trim();
    if (v.length === 0) return 'From is required.';
    if (v.length < 2) return 'From must be at least 2 characters.';
    return null;
  });

  readonly toError = computed(() => {
    const v = this.tourModel().to.trim();
    if (v.length === 0) return 'To is required.';
    if (v.length < 2) return 'To must be at least 2 characters.';
    return null;
  });

  readonly distanceError = computed(() => {
    const v = this.tourModel().distance;
    if (v === null || (v as any) === '') return 'Distance is required.';
    if (isNaN(v)) return 'Please enter a valid number.';
    if (v <= 0) return 'Distance must be greater than 0.';
    return null;
  });

  readonly estimatedTimeError = computed(() => {
    const v = this.tourModel().estimatedTime;

    if (v === null) return 'Estimated time is required.';
    if (isNaN(Number(v))) return 'Please enter a valid time.';
    if (Number(v) <= 0) return 'Estimated time must be greater than 0.';

    return null;
  });

  readonly canSubmit = computed(() =>
    !this.nameError() &&
    !this.descriptionError() &&
    !this.fromError() &&
    !this.toError() &&
    !this.distanceError() &&
    !this.estimatedTimeError()
  );

  clearError(): void {
    this.tourService.clearError();
  }

  saveTour(): void {
    this.clearError();

    if (!this.canSubmit()) {
      return;
    }

    const model = this.tourModel();

    const showSuccessAndNavigate = (message: string) => {
      const snackBarRef = this.snackBar.open(
        message,
        '',
        {
          duration: 1500,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['success-snackbar']
        }
      );

      snackBarRef.afterDismissed().subscribe(() => {
        this.router.navigate(['/my-tours']);
      });
    };

    if (this.id()) {
      this.tourService.updateTour(model, () => {
        showSuccessAndNavigate('Tour updated successfully!');
      });
    } else {
      this.tourService.addTour(model, () => {
        showSuccessAndNavigate('Tour saved successfully!');
      });
    }
  }

  calculateRoute(): void {
    const model = this.tourModel();

    this.routeService.calculateRoute({
      from: model.from,
      to: model.to,
      transportType: model.transportType
    }).subscribe({
      next: response => {
        this.tourModel.update(prev => ({
          ...prev,
          distance: Number(response.distanceKm.toFixed(2)),
          estimatedTime: response.durationMinutes
        }));

        this.mapFacadeService.setRoute(
          response.coordinates as [number, number][]
        );
      },
      error: () => {
        alert('Could not calculate route');
      }
    });
  }

  formatDistance(distance: number | null): string {
    if (distance === null || distance === undefined) {
      return '';
    }

    return `${distance.toFixed(2)} km`;
  }

  formatEstimatedTime(minutes: number | null): string {
    if (minutes === null || minutes === undefined) {
      return '';
    }

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (hours === 0) {
      return `${remainingMinutes} min`;
    }

    if (remainingMinutes === 0) {
      return `${hours} h`;
    }

    return `${hours} h ${remainingMinutes} min`;
  }
}
