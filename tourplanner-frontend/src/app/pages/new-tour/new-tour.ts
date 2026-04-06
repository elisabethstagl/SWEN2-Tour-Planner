import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Layout} from "../../layout/layout";
import {MatFormField, MatHint, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {provideNativeDateAdapter} from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTimepicker, MatTimepickerInput, MatTimepickerToggle} from '@angular/material/timepicker';


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
  ],
  templateUrl: './new-tour.html',
  styleUrl: './new-tour.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],
})
export class NewTour {

}
