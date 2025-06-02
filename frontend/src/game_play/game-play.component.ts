import {AfterViewInit, Component, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {GamePlay, GameState} from './game-play';
import {IMessage} from '@stomp/stompjs';
import {ChessCellComponent} from './chess-cell/chess-cell.component';
import {Figure} from './figure';
import {FigureColor, TimeControl} from '../game_conditions/game-conditions';
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
import {ChoiceDialogComponent} from './dialog-choice/choice-dialog.component';

@Component({
  selector: 'app-game-play',
  standalone: true,
  templateUrl: './game-play.component.html',
  styleUrls: ['./game-play.component.css'],
  imports: [FormsModule, NgForOf, ChessCellComponent, NgIf, ChatComponent, CountdownTimerComponent],
})
export class GamePlayComponent implements OnInit, AfterViewInit {
  @ViewChildren(ChessCellComponent) cells!: QueryList<ChessCellComponent>;
  @ViewChild('whiteTimer') whiteTimer!: CountdownTimerComponent; // Получаем доступ к таймеру создателя
  @ViewChild('blackTimer') blackTimer!: CountdownTimerComponent; // Получаем доступ к таймеру противника

  public gamePlay: GamePlay | null = null
  private selectedCell: ChessCellComponent | null = null;

  constructor(private route: ActivatedRoute,
              public userService: UserService,
              private gamePlayService: GamePlayService,
              private stompService: StompService,
              public dialog: MatDialog
  ) {
  }

  ngOnInit() {
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

      this.stompService.subscribe(`${wsGamePlay}/${gameId}/action`, message => this.onNewAction(message))
        .then(() => {
          return this.stompService.subscribe(`${wsGamePlay}/${gameId}/draw/request`, message => this.onDrawRequest(message))
            .then(() => {
              return this.stompService.subscribe(`${wsGamePlay}/${gameId}/draw/response`, message => this.onDrawResponse(message))
                .catch(error => {
                  console.error('Ошибка при подписке:', error);
                });
            });
        })
    })
  }

  ngAfterViewInit() {
    this.updateBoard(); // Проверяем и обновляем доску после инициализации представления
    this.startTimers();
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
      const action = this.findActionByCells(this.selectedCell, newSelectedCell);
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

  getPlayerLabel(figureColor: FigureColor) {
    return this.gamePlay!.gameConditions.creatorFigureColor!.code === figureColor.code ?
      `${this.gamePlay?.creatorLogin} (${this.gamePlay?.creatorRating})` :
      `${this.gamePlay?.opponentLogin} (${this.gamePlay?.opponentRating})`
  }

  getRowsIndexes() {
    return this.getFigureColorForCurrentUser().code === FigureColor.WHITE.code ?
      [7, 6, 5, 4, 3, 2, 1, 0] : [0, 1, 2, 3, 4, 5, 6, 7]
  }

  getColumnsIndexes() {
    return this.getFigureColorForCurrentUser().code === FigureColor.WHITE.code ?
      [0, 1, 2, 3, 4, 5, 6, 7] : [7, 6, 5, 4, 3, 2, 1, 0]
  }

  // Логика для предложения ничьей
  offerDraw() {
    console.log('Предложить ничью');

    this.dialog.open(ChoiceDialogComponent, {
      width: '250px',
      data: {title: 'Предложение ничьи', message: 'Вы уверены?'}
    }).afterClosed().subscribe((result: boolean) => {
      console.log('Диалог закрыт, результат:', result);
      if (result) {
        this.gamePlayService.requestDraw(this.gamePlay!.id, this.userService.user!.id)
          .subscribe(console.log, console.error)
      }
    });
  }

  // Логика для сдачи
  resign() {
    console.log('Сдаться');

    this.dialog.open(ChoiceDialogComponent, {
      width: '250px',
      data: {title: 'Сдаться', message: 'Вы уверены?'}
    }).afterClosed().subscribe((result: boolean) => {
      console.log('Диалог закрыт, результат:', result);
      if (result) {
        this.gamePlayService.resign(this.gamePlay!.id, this.userService.user!.id)
          .subscribe(console.log, console.error)
      }
    });
  }

  private startTimers() {
    // Убедитесь, что таймеры инициализированы
    if (this.whiteTimer && this.blackTimer && this.gamePlay && this.gamePlay.gameState.code === GameState.CONTINUES.code) {
      if (this.gamePlay.gameConditions.timeControl.code === TimeControl.WATCH.code) {
        const partyTime = this.gamePlay.gameConditions.partyTime * 60;

        const creatorFigureColor = this.gamePlay.gameConditions.creatorFigureColor;
        const whiteUserId = creatorFigureColor.code == FigureColor.WHITE.code ? this.gamePlay.creatorId : this.gamePlay.opponentId;
        const blackUserId = creatorFigureColor.code == FigureColor.BLACK.code ? this.gamePlay.creatorId : this.gamePlay.opponentId;

        this.whiteTimer.startTimer(this.gamePlay.id, whiteUserId, partyTime, () => this.timeout(whiteUserId));
        this.blackTimer.startTimer(this.gamePlay.id, blackUserId, partyTime, () => this.timeout(blackUserId));

        if (this.whiteTimer.userId === this.gamePlay!.activeUserId) {
          this.whiteTimer.resumeTimer()
          this.blackTimer.stopTimer()
        } else {
          this.blackTimer.resumeTimer()
          this.whiteTimer.stopTimer()
        }
      } else {
        this.whiteTimer.visible = false
        this.blackTimer.visible = false

      }
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
    if (this.gamePlay!.gameConditions.timeControl.code === TimeControl.WATCH.code) {
      if (this.whiteTimer.userId == this.gamePlay!.activeUserId) {
        this.whiteTimer.resumeTimer()
        this.blackTimer.countdown! += this.gamePlay!.gameConditions.moveTime
        this.blackTimer.stopTimer()
      } else {
        this.blackTimer.resumeTimer()
        this.whiteTimer.countdown! += this.gamePlay!.gameConditions.moveTime
        this.whiteTimer.stopTimer()
      }
    }
  }

  private executeAction(action: GameAction) {
    if (action.actionType.code === ActionType.SWAP.code) {
      const figureColor = this.getFigureColorForCurrentUser()

      this.dialog.open(ChessPieceDialogComponent, {data: {figureColor: figureColor}})
        .afterClosed().subscribe((figure: Figure) => {
        action.codeNotation = action.codeNotation.replace('X', figure.notationChar)
        this.sendAction(action)
      })

    } else {
      this.sendAction(action)
    }
  }

  private sendAction(action: GameAction) {
    this.gamePlayService.makeAction(this.gamePlay!.id, action.codeNotation).subscribe(
      () => {
        console.log(`made action: ${action.codeNotation}`)
        this.updateTimers();
      }, console.error
    )
  }

  private findActionByCells(selectedCell: ChessCellComponent, newSelectedCell: ChessCellComponent) {
    for (let action of this.getActionsForCurrentUser()) {
      const isMoveAction = action.startPosition === selectedCell.id &&
        (action.endPosition == newSelectedCell.id || action.eatenPosition == newSelectedCell.id);

      const isKingCastling = action.startPosition === selectedCell.id
        && action.endPosition === newSelectedCell.id

      if (isMoveAction || isKingCastling) {
        return action
      }
    }

    return null
  }

  private getActionsForCurrentUser() {
    if (this.gamePlay!.activeUserId !== this.userService.user?.id) {
      return [];
    }

    const figureColor = this.getFigureColorForActiveUser();
    return figureColor.code === FigureColor.WHITE.code ? this.gamePlay!.whiteActions : this.gamePlay!.blackActions;
  }


  private findActionsForCurrentUser(selectedCell: ChessCellComponent) {
    const actions: GameAction[] = []
    const actionsForCurrentUser = this.getActionsForCurrentUser();
    for (let action of actionsForCurrentUser) {
      if (action.startPosition === selectedCell.id) {
        actions.push(action)
      }
    }
    return actions
  }

  private viewActions(selectedCell: ChessCellComponent) {
    this.clearViewActions()
    const actions = this.findActionsForCurrentUser(selectedCell);
    for (let action of actions) {
      this.viewAction(action)
    }
  }

  private viewAction(action: GameAction) {
    const moveCell = this.cells.find(cell => cell.id === action.endPosition);
    const eatenCell = action.eatenPosition ? this.cells.find(cell => cell.id === action.eatenPosition) : null;

    switch (action.actionType) {
      case ActionType.MOVE:
      case ActionType.CASTLING: {
        moveCell!.isMove = true;
        break;
      }
      case ActionType.EAT: {
        if (eatenCell) {
          eatenCell!.isEat = true;
        }
        break;
      }
      case ActionType.TAKING: {
        if (moveCell) moveCell!.isMove = true;
        if (eatenCell) eatenCell!.isEat = true;
        break;
      }
      case ActionType.SWAP: {
        if (moveCell) moveCell!.isSwap = true;
        break;
      }
    }
  }

  private clearViewActions() {
    for (let cell of this.cells) {
      cell.clear()
    }
  }

  private checkGameEnd() {
    const gameState = this.gamePlay!.gameState;

    if (gameState !== GameState.CONTINUES) {
      this.blackTimer.stopTimer();
      this.whiteTimer.stopTimer();

      const figureColor = this.getFigureColorForCurrentUser();
      const isWhite = figureColor.code === FigureColor.WHITE.code;
      const isWin = (isWhite && gameState.whiteWin) || (!isWhite && gameState.blackWin);

      const isDraw = gameState === GameState.DRAW;
      const title = `Игра окончена! ${isDraw ? '' : isWin ? 'Вы победили!' : 'Вы проиграли!'}`;
      const message = isDraw ? 'Вы согласились на ничью' : gameState.title;

      this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: title,
          message: message
        }
      });
    }
  }


  private getFigureColorForActiveUser() {
    return this.gamePlay!.creatorId === this.gamePlay!.activeUserId ?
      this.gamePlay!.gameConditions.creatorFigureColor :
      this.gamePlay!.gameConditions.creatorFigureColor.reverseValue();
  }

  private getFigureColorForCurrentUser() {
    return this.gamePlay!.creatorId === this.userService.user!.id ?
      this.gamePlay!.gameConditions.creatorFigureColor :
      this.gamePlay!.gameConditions.creatorFigureColor.reverseValue();
  }

  private timeout(userId: string) {
    this.gamePlayService.timeout(this.gamePlay!.id, userId)
      .subscribe(console.log, console.error)
  }

  private onDrawRequest(message: IMessage) {
    const userId = message.body;
    if (userId === this.userService.user?.id) {
      this.dialog.open(ChoiceDialogComponent, {
        width: '250px',
        data: {title: 'Противник предлагает ничью', message: 'Вы согласны?'}
      }).afterClosed().subscribe((result: boolean) => {
        console.log('Диалог закрыт, результат:', result);
        this.gamePlayService.responseDraw(this.gamePlay!.id, this.userService.user!.id, result)
          .subscribe(console.log, console.error)
      });
    }
  }

  private onDrawResponse(message: IMessage) {
    const userId = message.body;
    if (this.userService.user!.id === userId) {
      this.dialog.open(ConfirmDialogComponent, {
        data: {title: 'Противник отказался от ничьи'}
      })
    }
  }

  protected readonly FigureColor = FigureColor;

  protected readonly Math = Math;
  protected readonly GameState = GameState;
}
