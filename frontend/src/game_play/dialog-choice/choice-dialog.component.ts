import { Component, Inject } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';


export interface DialogData {
  title: string;
  message: string;
}

@Component({
  selector: 'app-choice-dialog',
  standalone: true,
  templateUrl: './choice-dialog.component.html',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton
  ],
  styleUrls: ['./choice-dialog.component.css']
})
export class ChoiceDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ChoiceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
