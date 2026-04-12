import {Component, inject, input, Input, output} from '@angular/core';
import {TourLog} from '../../models/tour-log';
import {EditDeleteButtons} from "../edit-delete-buttons/edit-delete-buttons";
import {Router} from '@angular/router';
import {TourService} from '../../tour-service';

@Component({
  selector: 'app-tour-log-card',
  standalone: true,
    imports: [
        EditDeleteButtons
    ],
  templateUrl: './tour-log-card.html',
  styleUrl: './tour-log-card.css',
})

export class TourLogCard {
  private readonly router = inject(Router);
  private readonly tourService = inject(TourService);

  readonly log = input.required<TourLog>();

  readonly edit = output<number>();
  readonly delete = output<number>();

  onEdit() {
    this.edit.emit(this.log().id);
  }

  onDelete() {
    this.delete.emit(this.log().id);
  }
}
