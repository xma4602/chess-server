import { Component, Inject } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

export interface DialogData {
  title: string;
  message: string;
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  templateUrl: './confirm-dialog.component.html',
  imports: [
    MatDialogActions,
    MatButton,
    MatDialogContent,
    MatDialogTitle,
  ],
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  onOkClick(): void {
    this.dialogRef.close(true); // Возвращаем true при нажатии на кнопку ОК
  }
}
