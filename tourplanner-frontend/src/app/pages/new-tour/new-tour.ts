import {ChangeDetectionStrategy, Component, computed, inject, signal} from '@angular/core';
import {Layout} from "../../layout/layout";
import {MatFormField, MatHint, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTimepicker, MatTimepickerInput, MatTimepickerToggle} from '@angular/material/timepicker';
import {TourService} from '../../tour-service';
import {Tour, TransportType} from '../../models/tour';
import {Router} from '@angular/router';
import {MatButton} from '@angular/material/button';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-new-tour',
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
  templateUrl: './new-tour.html',
  styleUrl: './new-tour.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})
export class NewTour {
  private readonly tourService = inject(TourService);
  private readonly router = inject(Router);

  readonly error = this.tourService.error;

  readonly transportTypes: TransportType[] = ['walk', 'bike', 'car', 'public transport'];

  readonly name = signal('');
  readonly description = signal('');
  readonly from = signal('');
  readonly to = signal('');
  readonly transportType = signal<TransportType>('walk');
  readonly distance = signal<number | null>(null);
  readonly estimatedTime = signal('');
  readonly mapUrl = signal('');

  readonly nameError = computed<string | null>(() => {
    const v = this.name().trim();
    if (v.length === 0) return 'Tour name is required.';
    if (v.length < 2) return 'Tour name must be at least 2 characters.';
    return null;
  });

  readonly descriptionError = computed<string | null>(() => {
    const v = this.description().trim();
    if (v.length === 0) return 'Description is required.';
    if (v.length < 2) return 'Description must be at least 2 characters.';
    return null;
  });

  readonly fromError = computed<string | null>(() => {
    const v = this.from().trim();
    if (v.length === 0) return 'From is required.';
    if (v.length < 2) return 'From must be at least 2 characters.';
    return null;
  });

  readonly toError = computed<string | null>(() => {
    const v = this.to().trim();
    if (v.length === 0) return 'To is required.';
    if (v.length < 2) return 'To must be at least 2 characters.';
    return null;
  });

  readonly distanceError = computed<string | null>(() => {
    const v = this.distance();
    if (v === null) return 'Distance is required.';
    if (v <= 0) return 'Distance must be greater than 0.';
    return null;
  });

  readonly estimatedTimeError = computed<string | null>(() => {
    const v = this.estimatedTime().trim();
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

    if (!this.canSubmit()) {
      return;
    }

    const createdId = this.tourService.addTour({
      userId: 1,
      name: this.name(),
      description: this.description(),
      from: this.from(),
      to: this.to(),
      transportType: this.transportType(),
      distance: this.distance() ?? 0,
      estimatedTime: this.estimatedTime(),
    });

    if (createdId !== null) {
      this.router.navigate(['/tour', createdId]);
    }
  }
}
