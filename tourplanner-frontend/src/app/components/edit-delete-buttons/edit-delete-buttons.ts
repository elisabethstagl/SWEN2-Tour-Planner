import {Component, output} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatFabButton, MatMiniFabButton} from '@angular/material/button';

@Component({
  selector: 'app-edit-delete-buttons',
  imports: [
    MatIcon,
    MatMiniFabButton
  ],
  templateUrl: './edit-delete-buttons.html',
  styleUrl: './edit-delete-buttons.css',
})
export class EditDeleteButtons {
  readonly editClicked = output<void>();
  readonly deleteClicked = output<void>();

}
