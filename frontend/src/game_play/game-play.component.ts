import {Component, QueryList, ViewChildren} from '@angular/core';
import {NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {GamePlayService} from './game-play-service';
import {StompService} from '../stomp.service';
import {GamePlay} from './game-play';
import {IMessage} from '@stomp/stompjs';
import {ChessCellComponent} from './chess-cell/chess-cell.component';
import {Figure} from './figure';
import {FigureColor} from '../game_conditions/game-conditions';

@Component({
  selector: 'app-game-play',
  standalone: true,
  templateUrl: './game-play.component.html',
  styleUrls: ['./game-play.component.css'],
  imports: [FormsModule, NgForOf, HttpClientModule, ChessCellComponent],
})
export class GamePlayComponent {
  @ViewChildren(ChessCellComponent) cells!: QueryList<ChessCellComponent>;
  private gamePlay?: GamePlay = new GamePlay(
    'game123',
    'user1',
    'user2',
    'CreatorLogin',
    'OpponentLogin',
    true,
    FigureColor.WHITE,
    30,
    new Date().toISOString(),
    false
  );
  private cell?: ChessCellComponent
  private reverse: boolean = false;

  constructor(private router: Router,
              private route: ActivatedRoute,
              // private gamePlayService: GamePlayService,
              // private stompService: StompService
  ) {
  }

  ngOnInit() {
    // this.route.paramMap.subscribe(params => {
    //   const gameId = params.get('id')!;
    //   this.gamePlayService.getGamePlay(gameId).subscribe(
    //     gamePlay => this.gamePlay = gamePlay,
    //     console.log
    //   )
    //
    //   this.stompService.stompClient.subscribe(`/games/${gameId}/action`, this.onAction)
    //
    // })
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.initFigures();
    });
  }

  private initFigures() {
    // Устанавливаем фигуры для черных
    this.setPieceInCell('a8', Figure.B_ROOK);
    this.setPieceInCell('b8', Figure.B_KNIGHT);
    this.setPieceInCell('c8', Figure.B_BISHOP);
    this.setPieceInCell('d8', Figure.B_QUEEN);
    this.setPieceInCell('e8', Figure.B_KING);
    this.setPieceInCell('f8', Figure.B_BISHOP);
    this.setPieceInCell('g8', Figure.B_KNIGHT);
    this.setPieceInCell('h8', Figure.B_ROOK);

    // Устанавливаем пешки для черных
    for (let i = 0; i < 8; i++) {
      const cellId = String.fromCharCode(97 + i) + '7'; // 'a7', 'b7', ..., 'h7'
      this.setPieceInCell(cellId, Figure.B_PAWN);
    }

    // Устанавливаем фигуры для белых
    this.setPieceInCell('a1', Figure.W_ROOK);
    this.setPieceInCell('b1', Figure.W_KNIGHT);
    this.setPieceInCell('c1', Figure.W_BISHOP);
    this.setPieceInCell('d1', Figure.W_QUEEN);
    this.setPieceInCell('e1', Figure.W_KING);
    this.setPieceInCell('f1', Figure.W_BISHOP);
    this.setPieceInCell('g1', Figure.W_KNIGHT);
    this.setPieceInCell('h1', Figure.W_ROOK);

    // Устанавливаем пешки для белых
    for (let i = 0; i < 8; i++) {
      const cellId = String.fromCharCode(97 + i) + '2'; // 'a2', 'b2', ..., 'h2'
      this.setPieceInCell(cellId, Figure.W_PAWN);
    }
  }

  getRowsIndexes() {
    return this.reverse ? [0, 1, 2, 3, 4, 5, 6, 7] : [7, 6, 5, 4, 3, 2, 1, 0]
  }

  // Метод для установки фигуры в ячейку по id
  setPieceInCell(cellId: string, figure: Figure) {
    const cell = this.cells.find(c => c.id === cellId)!;
    cell.figure = figure;
  }

  private onAction(message: IMessage) {

  }

  getCellId(rowIndex: number, colIndex: number): string {
    const columnLetter = String.fromCharCode(97 + colIndex); // 'a' + colIndex
    return `${columnLetter}${rowIndex + 1}`; // Формат: a1, b1, ..., h8
  }

  getColumnLabel(colIndex: number): string {
    return String.fromCharCode(97 + colIndex); // 'a' + colIndex
  }

  onClick(selectedCell: ChessCellComponent) {
    console.log('Clicked cell:', selectedCell.id);
    selectedCell.select(true)
    this.cells.forEach(cell => {
      if (cell !== selectedCell) {
        cell.select(false); // Снимаем выделение
      }
    });
  }
}
