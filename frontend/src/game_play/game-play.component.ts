import {AfterViewInit, Component, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {GamePlay, GameState} from './game-play';
import {IMessage} from '@stomp/stompjs';
import {ChessCellComponent} from './chess-cell/chess-cell.component';
import {Figure} from './figure';
import {FigureColor} from '../game_conditions/game-conditions';
import {ActionType, GameAction} from './game-action';
import {UserService} from '../user/user-service';
import {StompService} from '../stomp.service';
import {GamePlayService} from './game-play-service';
import {wsGamePlay} from '../data.service';
import {ChatComponent} from './chat/chat.component';
import {ConfirmDialogComponent} from './dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {ChessPieceDialogComponent} from './chess-select/ chess-piece-dialog.component';
import {CountdownTimerComponent} from './timer/countdown-timer.component';

@Component({
  selector: 'app-game-play',
  standalone: true,
  templateUrl: './game-play.component.html',
  styleUrls: ['./game-play.component.css'],
  imports: [FormsModule, NgForOf, HttpClientModule, ChessCellComponent, NgIf, ChatComponent, CountdownTimerComponent],
})
export class GamePlayComponent implements OnInit, AfterViewInit {
  @ViewChildren(ChessCellComponent) cells!: QueryList<ChessCellComponent>;
  @ViewChild('creatorTimer') creatorTimer!: CountdownTimerComponent; // Получаем доступ к таймеру создателя
  @ViewChild('opponentTimer') opponentTimer!: CountdownTimerComponent; // Получаем доступ к таймеру противника

  public gamePlay: GamePlay | null = null
  private selectedCell: ChessCellComponent | null = null;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private userService: UserService,
              private gamePlayService: GamePlayService,
              private stompService: StompService,
              public dialog: MatDialog
  ) {
  }

  ngOnInit() {

  }

  ngAfterViewInit() {
    this.updateBoard(); // Проверяем и обновляем доску после инициализации представления

    this.route.paramMap.subscribe(params => {
      const gameId = params.get('id')!;
      this.gamePlayService.getGamePlay(gameId).subscribe(
        gamePlay => {
          this.gamePlay = gamePlay;
          this.updateBoard(); // Проверяем и обновляем доску
          this.startTimers();
        },
        console.log
      )

      this.stompService.subscribe(`${wsGamePlay}/${gameId}/action`, this.onNewAction.bind(this))

    })
  }

  private startTimers() {
    // Убедитесь, что таймеры инициализированы
    if (this.creatorTimer && this.opponentTimer && this.gamePlay) {
      const partyTime = this.gamePlay.gameConditions.partyTime * 60;
      this.creatorTimer.startTimer(this.gamePlay.creatorLogin, this.gamePlay.creatorId, partyTime,
        () => this.timeout(this.gamePlay!.creatorId));
      this.opponentTimer.startTimer(this.gamePlay.opponentLogin, this.gamePlay.opponentId, partyTime,
        () => this.timeout(this.gamePlay!.creatorId));

      this.updateTimers();

    } else {
      console.error('Таймеры или gamePlay не инициализированы');
    }
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


  private onNewAction(message: IMessage) {
    console.log(`new action`)
    const obj = JSON.parse(message.body);
    this.gamePlay = GamePlay.fromObject(obj)
    this.updateBoard();
    this.updateTimers();
    this.checkGameEnd();
  }

  private updateTimers() {
    if (this.creatorTimer.userId == this.gamePlay!.activeUserId) {
      this.creatorTimer.resumeTimer()
      this.opponentTimer.stopTimer()
    } else {
      this.opponentTimer.resumeTimer()
      this.creatorTimer.stopTimer()
    }
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

  private executeAction(action: GameAction) {
    if (action.actionType.code === ActionType.SWAP.code) {
      const figureColor = this.gamePlay!.creatorId === this.gamePlay!.activeUserId ?
        this.gamePlay!.gameConditions.creatorFigureColor :
        this.gamePlay!.gameConditions.creatorFigureColor.reverseValue()

      this.dialog.open(ChessPieceDialogComponent, {data: {figureColor: figureColor}})
        .afterClosed().subscribe((figure: Figure) => {
        action.actionNotation = action.actionNotation.replace('X', figure.notationChar)
        this.sendAction(action)
      })

    } else {
      this.sendAction(action)
    }
  }

  private sendAction(action: GameAction) {
    this.gamePlayService.makeAction(this.gamePlay!.id, action.actionNotation).subscribe(
      () => {
        console.log(`made action: ${action.actionNotation}`)
        this.updateTimers();
      }, console.error
    )
  }


  private findAction(selectedCell: ChessCellComponent, newSelectedCell: ChessCellComponent) {
    for (let action of this.getActionForCurrentUser()) {
      const isMoveAction = action.startPosition === selectedCell.id &&
        (action.endPosition == newSelectedCell.id || action.eatenPosition == newSelectedCell.id);

      const isKingCastling = action.kingStartPosition === selectedCell.id
        && action.kingEndPosition === newSelectedCell.id

      const isRookCastling = action.rookStartPosition === selectedCell.id
        && action.rookEndPosition === newSelectedCell.id

      if (isMoveAction || isKingCastling || isRookCastling)
        return action
    }

    return null
  }

  getActionForCurrentUser() {
    const figureColor = this.getFigureColorForCurrentUser();
    return figureColor.code === FigureColor.WHITE.code ?
          this.gamePlay!.whiteActions : this.gamePlay!.blackActions
  }

  private findActions(selectedCell: ChessCellComponent) {
    const actions: GameAction[] = []
    for (let action of this.getActionForCurrentUser()) {
      if (action.startPosition === selectedCell.id
        || action.kingStartPosition === selectedCell.id
        || action.rookStartPosition === selectedCell.id) {
        actions.push(action)
      }
    }
    return actions
  }

  private viewActions(selectedCell: ChessCellComponent) {
    this.clearViewActions()
    const actions = this.findActions(selectedCell);
    for (let action of actions) {
      this.viewAction(selectedCell, action)
    }
  }

  private viewAction(selectedCell: ChessCellComponent, action: GameAction) {
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
        if (selectedCell.figure!.notationChar == 'K') {
          const moveCell = this.cells.find(cell => cell.id === action.kingEndPosition);
          moveCell!.isMove = true;
        } else {
          const moveCell = this.cells.find(cell => cell.id === action.rookEndPosition);
          moveCell!.isMove = true;
        }
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

  protected readonly Math = Math;

  private checkGameEnd() {
    const gameState = this.gamePlay!.gameState;
    if (gameState !== GameState.CONTINUES) {
      const figureColor = this.getFigureColorForCurrentUser()

      if ((figureColor.code === FigureColor.WHITE.code && gameState === GameState.WHITE_WIN)
        || (figureColor.code === FigureColor.BLACK.code && gameState === GameState.BLACK_WIN)) {

        this.opponentTimer.stopTimer()
        this.creatorTimer.stopTimer()

        this.dialog.open(ConfirmDialogComponent, {
          data: {
            title: 'Игра окончена!',
            message: 'Вы победили!'
          }
        }).afterClosed().subscribe(
          () => this.router.navigate(['']), console.error
        )
      } else {
        this.dialog.open(ConfirmDialogComponent, {
          data: {
            title: 'Игра окончена!',
            message: 'Вы проиграли!'
          }
        }).afterClosed().subscribe(
          () => this.router.navigate(['']), console.error
        )
      }
    }
  }

  private getFigureColorForCurrentUser() {
    return this.gamePlay!.creatorId === this.gamePlay?.activeUserId ?
      this.gamePlay!.gameConditions.creatorFigureColor :
      this.gamePlay!.gameConditions.creatorFigureColor.reverseValue();
  }

  private timeout(userId: string) {
    this.gamePlayService.timeout(this.gamePlay!.id, userId)
      .subscribe(console.log, console.error)
  }
}
