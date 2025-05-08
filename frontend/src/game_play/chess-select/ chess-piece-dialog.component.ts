import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {NgForOf} from '@angular/common';
import {FigureColor} from '../../game_conditions/game-conditions';
import {Figure} from '../figure';

@Component({
  selector: 'app-chess-piece-dialog',
  standalone: true,
  templateUrl: './chess-piece-dialog.component.html',
  imports: [
    MatDialogTitle,
    NgForOf,
    MatDialogContent,
  ],
  styleUrls: ['./chess-piece-dialog.component.css']
})
export class ChessPieceDialogComponent {

  constructor(public dialogRef: MatDialogRef<ChessPieceDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { figureColor: FigureColor }) {
  }

  getFigures() {
    return this.data.figureColor.code === FigureColor.WHITE.code ?
      [Figure.W_QUEEN, Figure.W_KNIGHT, Figure.W_BISHOP, Figure.W_ROOK] :
      [Figure.B_QUEEN, Figure.B_KNIGHT, Figure.B_BISHOP, Figure.B_ROOK];
  }

  onSelect(piece: Figure): void {
    this.dialogRef.close(piece);
  }

}
