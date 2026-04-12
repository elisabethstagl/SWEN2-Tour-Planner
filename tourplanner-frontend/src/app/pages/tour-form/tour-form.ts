import { ChangeDetectionStrategy, Component, computed, inject, signal, input, effect } from '@angular/core';
import { Layout } from "../../layout/layout";
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TourService } from '../../tour-service';
import { Tour, TransportType } from '../../models/tour';
import { Router } from '@angular/router';
import { MatButton } from '@angular/material/button';
import { FormsModule } from '@angular/forms';

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
  ],
  templateUrl: './tour-form.html',
  styleUrl: './tour-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})
export class TourForm {
  private readonly tourService = inject(TourService);
  private readonly router = inject(Router);

  readonly id = input<number, string | undefined>(undefined, {
    transform: (v) => (v === undefined ? undefined : Number(v))
  });

  readonly error = this.tourService.error;
  readonly transportTypes: TransportType[] = ['walk', 'bike', 'car', 'public transport'];

  readonly tourModel = signal<Tour>({
    id: 0,
    userId: 1,
    name: '',
    description: '',
    from: '',
    to: '',
    transportType: 'walk',
    distance: null as any,
    estimatedTime: '',
  });

  constructor() {
    effect(() => {
      const tourId = this.id();
      if (tourId) {
        const existingTour = this.tourService.getTourById(tourId)();
        if (existingTour) {
          this.tourModel.set({ ...existingTour });
        }
      }
    });
  }

  updateField<K extends keyof Tour>(field: K, value: any): void {
    let finalValue = value;

    if (field === 'distance') {
      finalValue = value === '' || value === null ? null : Number(value);
    }

    this.tourModel.update(prev => ({ ...prev, [field]: finalValue }));
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
    const v = this.tourModel().estimatedTime.trim();
    if (v.length === 0) return 'Estimated time is required.';
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
    if (!this.canSubmit()) return;

    const model = this.tourModel();
    let resultId: number | null;

    if (this.id()) {
      this.tourService.updateTour(model);
      resultId = model.id;
    } else {
      resultId = this.tourService.addTour(model);
    }

    if (resultId) {
      this.router.navigate(['/tour', resultId]);
    }
  }
}
