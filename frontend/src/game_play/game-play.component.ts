import {Component, QueryList, OnInit, ViewChildren, AfterViewInit} from '@angular/core';
import {NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {GamePlay} from './game-play';
import {IMessage} from '@stomp/stompjs';
import {ChessCellComponent} from './chess-cell/chess-cell.component';
import {Figure} from './figure';
import {FigureColor} from '../game_conditions/game-conditions';
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

  private gamePlay: GamePlay | null = null
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
        gamePlay => {
          this.gamePlay = gamePlay;
          this.updateBoard(); // Проверяем и обновляем доску
        },
        console.log
      )

      this.stompService.subscribe(`${wsGamePlay}/${gameId}/action`, this.onAction.bind(this))

    })

  }

  ngAfterViewInit() {
    this.updateBoard(); // Проверяем и обновляем доску после инициализации представления
  }


  private updateBoard() {
    if (this.gamePlay && this.cells) { // Проверяем, что gamePlay загружен и cells инициализированы
      this.clearViewActions()
      for (let cell of this.cells) {
        const figureCode = this.gamePlay.figures.get(cell.id);
        cell.figure = figureCode === undefined ? null : Figure.fromCode(figureCode);
      }
    }
  }


  private onAction(message: IMessage) {
    console.log(`new action`)
    const obj = JSON.parse(message.body);
    this.gamePlay = GamePlay.fromObject(obj)
    this.updateBoard();
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
        this.viewActions(this.selectedCell)
        this.selectedCell.isSelected = true
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
        this.viewActions(this.selectedCell)
        this.selectedCell.isSelected = true
        console.log('4')
      }
    }
  }

  // isActivePlayer(){
  //   this.gamePlay.
  // }

  private executeAction(action: GameAction) {
    this.gamePlayService.makeAction(this.gamePlay!.id, action.actionNotation).subscribe(
      () => console.log(`made action: ${action}`)
    )
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
    if (this.userService.user!.id == this.gamePlay!.creatorId) {
      return this.gamePlay!.gameConditions.figureColor == FigureColor.WHITE ?
        this.gamePlay!.whiteActions : this.gamePlay!.blackActions
    } else {
      return this.gamePlay!.gameConditions.figureColor == FigureColor.WHITE ?
        this.gamePlay!.blackActions : this.gamePlay!.whiteActions
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
    this.clearViewActions()
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

  private clearViewActions() {
    for (let cell of this.cells) {
      cell.clear()
    }
  }
}
