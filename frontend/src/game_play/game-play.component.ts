import {Component, QueryList, OnInit, ViewChildren, AfterViewInit} from '@angular/core';
import {NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {GamePlay} from './game-play';
import {IMessage} from '@stomp/stompjs';
import {ChessCellComponent} from './chess-cell/chess-cell.component';
import {Figure} from './figure';
import {FigureColor, GameConditions} from '../game_conditions/game-conditions';
import {ActionType, GameAction} from './game-action';
import {UserService} from '../user/user-service';
import {StompService} from '../stomp.service';
import {GamePlayService} from './game-play-service';
import {wsGamePlay} from '../data.service';

@Component({
  selector: 'app-game-play',
  standalone: true,
  templateUrl: './game-play.component.html',
  styleUrls: ['./game-play.component.css'],
  imports: [FormsModule, NgForOf, HttpClientModule, ChessCellComponent],
})
export class GamePlayComponent implements OnInit, AfterViewInit {
  @ViewChildren(ChessCellComponent) cells!: QueryList<ChessCellComponent>;
  private gamePlay: GamePlay = new GamePlay(
    'game123',
    'user1',
    'user2',
    'CreatorLogin',
    'OpponentLogin',
    new GameConditions(5, 3, FigureColor.WHITE),
    [
      new GameAction('e2-e4', ActionType.MOVE, 'e2', 'e4')
    ],
    [],
    new Map([
      ['a8', 'BLACK_ROOK'],
      ['b8', 'BLACK_KNIGHT'],
      ['c8', 'BLACK_BISHOP'],
      ['d8', 'BLACK_QUEEN'],
      ['e8', 'BLACK_KING'],
      ['f8', 'BLACK_BISHOP'],
      ['g8', 'BLACK_KNIGHT'],
      ['h8', 'BLACK_ROOK'],
      ['a7', 'BLACK_PAWN'],
      ['b7', 'BLACK_PAWN'],
      ['c7', 'BLACK_PAWN'],
      ['d7', 'BLACK_PAWN'],
      ['e7', 'BLACK_PAWN'],
      ['f7', 'BLACK_PAWN'],
      ['g7', 'BLACK_PAWN'],
      ['h7', 'BLACK_PAWN'],
      ['a1', 'WHITE_ROOK'],
      ['b1', 'WHITE_KNIGHT'],
      ['c1', 'WHITE_BISHOP'],
      ['d1', 'WHITE_QUEEN'],
      ['e1', 'WHITE_KING'],
      ['f1', 'WHITE_BISHOP'],
      ['g1', 'WHITE_KNIGHT'],
      ['h1', 'WHITE_ROOK'],
      ['a2', 'WHITE_PAWN'],
      ['b2', 'WHITE_PAWN'],
      ['c2', 'WHITE_PAWN'],
      ['d2', 'WHITE_PAWN'],
      ['e2', 'WHITE_PAWN'],
      ['f2', 'WHITE_PAWN'],
      ['g2', 'WHITE_PAWN'],
      ['h2', 'WHITE_PAWN'],
    ])
  );
  private selectedCell: ChessCellComponent | null = null;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private userService: UserService,
              private gamePlayService: GamePlayService,
              private stompService: StompService
  ) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const gameId = params.get('id')!;
      this.gamePlayService.getGamePlay(gameId).subscribe(
        gamePlay => this.gamePlay = gamePlay,
        console.log
      )

      this.stompService.stompClient.subscribe(`${wsGamePlay}/${gameId}/action`, this.onAction)

    })

  }

  ngAfterViewInit() {
    setTimeout(() => {
      for (let cell of this.gamePlay.figures) {
        const figure = Figure.fromCode(cell[1])!
        this.setPieceInCell(cell[0], figure)
      }
    });
  }

  getRowsIndexes() {
    let reverse
    if (this.userService.user!.id == this.gamePlay.creatorId) {
      reverse = this.gamePlay.gameConditions.figureColor == FigureColor.BLACK
    } else {
      reverse = this.gamePlay.gameConditions.figureColor == FigureColor.WHITE
    }

    return reverse ? [0, 1, 2, 3, 4, 5, 6, 7] : [7, 6, 5, 4, 3, 2, 1, 0]
  }

  // Метод для установки фигуры в ячейку по id
  setPieceInCell(cellId: string, figure: Figure) {
    const cell = this.cells.find(c => c.id === cellId)!;
    cell.figure = figure;
  }

  private onAction(message: IMessage) {
    this.gamePlay = JSON.parse(message.body)
  }

  getCellId(rowIndex: number, colIndex: number): string {
    const columnLetter = String.fromCharCode(97 + colIndex); // 'a' + colIndex
    return `${columnLetter}${rowIndex + 1}`; // Формат: a1, b1, ..., h8
  }

  onClick(newSelectedCell: ChessCellComponent) {
    console.log(`selectedCell: ${this.selectedCell?.id ?? 'null'}, clicked cell: ${newSelectedCell.id}`);

    if (this.selectedCell === null) {
      if (newSelectedCell.figure !== null) {
        this.selectedCell = newSelectedCell
        this.selectedCell.isSelected = true
        this.viewActions(this.selectedCell)
        console.log('1')
      } else {
        this.selectedCell = newSelectedCell
        this.selectedCell.isSelected = true
        console.log('2')
      }
    } else {
      const action = this.findAction(this.selectedCell, newSelectedCell);
      if (action !== null) {
        this.executeAction(action)
        console.log('3')
      } else {
        this.selectedCell.isSelected = false
        this.selectedCell = newSelectedCell
        this.selectedCell.isSelected = true
        this.viewActions(this.selectedCell)
        console.log('4')
      }
    }
  }

  private executeAction(action: GameAction) {
    this.gamePlayService.makeAction(this.gamePlay.id, action.actionNotation)
  }

  private findAction(selectedCell: ChessCellComponent, newSelectedCell: ChessCellComponent) {
    for (let action of this.getActionForCurrentUser()) {
      if (action.startPosition === selectedCell.id
        && (action.endPosition == newSelectedCell.id || action.eatenPosition == newSelectedCell.id)) {
        return action
      }
    }
    return null
  }

  getActionForCurrentUser() {
    if (this.userService.user!.id == this.gamePlay.creatorId) {
      return this.gamePlay.gameConditions.figureColor == FigureColor.WHITE ?
        this.gamePlay.whiteActions : this.gamePlay.blackActions
    } else {
      return this.gamePlay.gameConditions.figureColor == FigureColor.WHITE ?
        this.gamePlay.blackActions : this.gamePlay.whiteActions
    }
  }

  private findActions(selectedCell: ChessCellComponent) {
    const actions: GameAction[] = []
    for (let action of this.getActionForCurrentUser()) {
      if (action.startPosition === selectedCell.id) {
        actions.push(action)
      }
    }
    return actions
  }

  private viewActions(selectedCell: ChessCellComponent) {
    this.clearViewActions(selectedCell)
    const actions = this.findActions(selectedCell);
    for (let action of actions) {
      this.viewAction(action)
    }
  }

  private viewAction(action: GameAction) {
    switch (action.actionType) {
      case ActionType.MOVE: {
        const moveCell = this.cells.find(cell => cell.id === action.endPosition);
        moveCell!.isMove = true;
      }
        break;
      case ActionType.EAT: {
        const eatenCell = this.cells.find(cell => cell.id === action.eatenPosition);
        eatenCell!.isEat = true;
      }
        break;
      case ActionType.TAKING: {
        const eatenCell = this.cells.find(cell => cell.id === action.eatenPosition);
        const moveCell = this.cells.find(cell => cell.id === action.endPosition);
        moveCell!.isMove = true;
        eatenCell!.isEat = true;
      }
        break;
      case ActionType.CASTLING: {
        const moveCell = this.cells.find(cell => cell.id === action.rookStartPosition);
        moveCell!.isMove = true;
      }
        break;
      case ActionType.SWAP: {
        const moveCell = this.cells.find(cell => cell.id === action.endPosition);
        moveCell!.isSwap = true;
      }
    }
  }

  private clearViewActions(selectedCell: ChessCellComponent) {
    for (let cell of this.cells) {
      if (cell.id !== selectedCell.id) {
        cell.clear()
      }
    }
  }
}
