import {ChangeDetectionStrategy, Component, computed, effect, inject, input, signal} from '@angular/core';
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
  selector: 'app-tour-log-form',
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
  templateUrl: './tour-log-form.html',
  styleUrl: './tour-log-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})

export class TourLogForm {
  private tourService = inject(TourService);
  private router = inject(Router);

  readonly error = this.tourService.error;
  readonly tourId = input.required<number, string>({ transform: Number });
  readonly logId = input<number, string | undefined>(undefined, {
    transform: (v) => v ? Number(v) : undefined
  });

  readonly logModel = signal<TourLog>({
    id: 0,
    tourId: 0,
    date: '',
    time: '',
    comment: '',
    difficulty: 'medium',
    totalDistance: null as any,
    totalTime: '',
    rating: 0
  });

  readonly pickerDate = signal<Date | null>(null);
  readonly pickerTime = signal<Date | null>(null);

  constructor() {
    effect(() => {
      const lId = this.logId();
      if (lId) {
        const existingLog = this.tourService.logs().find(l => l.id === lId);
        if (existingLog) {
          this.logModel.set({ ...existingLog });
          this.pickerDate.set(new Date(existingLog.date));
          const [hrs, mins] = existingLog.time.split(':');
          const d = new Date();
          d.setHours(+hrs, +mins);
          this.pickerTime.set(d);
        }
      }
    });
  }

  updateField<K extends keyof TourLog>(field: K, value: any) {
    let finalValue = value;

    if (field === 'totalDistance') {
      const parsed = parseFloat(value);
      finalValue = isNaN(parsed) ? null : parsed;
    }

    this.logModel.update(prev => ({ ...prev, [field]: finalValue }));
    this.clearError();
  }

  updateDate(d: Date | null) {
    this.pickerDate.set(d);
    this.updateField('date', d?.toISOString().split('T')[0] || '');
  }

  updateTime(t: Date | null) {
    this.pickerTime.set(t);
    this.updateField('time', t?.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) || '');
  }

  setRating(v: number) {
    this.updateField('rating', v);
  }

  readonly dateError = computed(() => !this.logModel().date ? 'Date is required.' : null);
  readonly timeError = computed(() => !this.logModel().time ? 'Start time is required.' : null);
  readonly distanceError = computed(() => {
    const v = this.logModel().totalDistance;
    if (v === null || (v as any) === '') return 'Distance is required.';
    if (isNaN(v)) return 'Must be a valid number.';
    return v <= 0 ? 'Must be > 0.' : null;
  });

  readonly totalTimeError = computed(() => {
    const v = this.logModel().totalTime.trim();
    if (!v) return 'Duration is required.';
    return null;
  });

  readonly canSubmit = computed(() =>
    !this.dateError() && !this.timeError() && !this.distanceError() &&
    !this.totalTimeError() && this.logModel().rating > 0
  );

  stars = [1, 2, 3, 4, 5];
  clearError() { this.tourService.clearError(); }

  saveLog() {
    if (!this.canSubmit()) return;

    const model = { ...this.logModel(), tourId: this.tourId() };
    const successId = this.logId()
      ? (this.tourService.updateLog(model), model.id)
      : this.tourService.addLog(model);

    if (successId) this.router.navigate(['/tour-detail', this.tourId()]);
  }
}
