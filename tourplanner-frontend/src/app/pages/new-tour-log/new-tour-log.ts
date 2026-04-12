import {ChangeDetectionStrategy, Component, computed, inject, input, signal} from '@angular/core';
import {Layout} from '../../layout/layout';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatOption, provideNativeDateAdapter} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {MatTimepicker, MatTimepickerInput, MatTimepickerToggle} from '@angular/material/timepicker';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {TourService} from '../../tour-service';
import {Router} from '@angular/router';
import {TourLog} from '../../models/tour-log';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-new-tour-log',
  standalone: true,
  imports: [
    Layout,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatFormField,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatSuffix,
    MatTimepicker,
    MatTimepickerInput,
    MatTimepickerToggle,
    MatButton,
    MatIcon,
    FormsModule
  ],
  templateUrl: './new-tour-log.html',
  styleUrl: './new-tour-log.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})

export class NewTourLog {
  private tourService = inject(TourService);
  private router = inject(Router);

  readonly error = this.tourService.error;
  readonly id = input.required<number, string | number>({transform: Number});

  readonly date = signal<Date | null>(null);
  readonly time = signal<Date | null>(null); // MatTimepicker gibt ein Date-Objekt zurück
  readonly distance = signal<number | null>(null);
  readonly totalTime = signal(''); // Dauer als String (z.B. "02:30")
  readonly difficulty = signal<'easy' | 'medium' | 'hard'>('medium');
  readonly rating = signal(0);
  readonly comment = signal('');

  readonly dateError = computed(() => !this.date() ? 'Date is required.' : null);
  readonly timeError = computed(() => !this.time() ? 'Start time is required.' : null);
  readonly distanceError = computed<string | null>(() => {
    const v = this.distance();
    if (v === null || (v as any) === '') return 'Distance is required.';
    if (isNaN(v)) return 'Must be a valid number.'; // Der NaN-Check
    if (v <= 0) return 'Distance must be greater than 0.';
    return null;
  });

  readonly totalTimeError = computed<string | null>(() => {
    const v = this.totalTime().trim();
    const timeRegex = /^\d{1,2}:[0-5]\d$/;

    if (v.length === 0) return 'Duration is required.';
    if (!timeRegex.test(v)) return 'Use format H:mm (e.g. 4:30).';
    return null;
  });

  readonly canSubmit = computed(() =>
    !this.dateError() &&
    !this.timeError() &&
    !this.distanceError() &&
    !this.totalTimeError() &&
    this.rating() > 0
  );

  stars = [1, 2, 3, 4, 5];

  setRating(v: number) {
    this.rating.set(v);
    this.clearError();
  }

  clearError() {
    this.tourService.clearError();
  }

  saveLog() {
    if (!this.canSubmit()) return;

    const tourId = this.id();
    const successId = this.tourService.addLog({
      tourId: tourId,
      date: this.date()?.toISOString().split('T')[0] || '',
      time: this.time()?.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'}) || '',
      comment: this.comment(),
      difficulty: this.difficulty(),
      totalDistance: this.distance() ?? 0,
      totalTime: this.totalTime(),
      rating: this.rating()
    });

    if (successId !== null) {
      this.router.navigate(['/tour-detail', tourId]);
    }
  }
}
