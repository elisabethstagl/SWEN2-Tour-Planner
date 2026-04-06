import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {Layout} from '../../layout/layout';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MatFormField, MatHint, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatOption, provideNativeDateAdapter} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {MatTimepicker, MatTimepickerInput, MatTimepickerToggle} from '@angular/material/timepicker';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-new-tour-log',
  standalone: true,
  imports: [
    Layout,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatSuffix,
    MatTimepicker,
    MatTimepickerInput,
    MatTimepickerToggle,
    MatButton,
    MatIcon
  ],
  templateUrl: './new-tour-log.html',
  styleUrl: './new-tour-log.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})
export class NewTourLog {
  stars = [1, 2, 3, 4, 5];
  rating = signal(0);

  setRating(value: number) {
    this.rating.set(value);
  }

}
