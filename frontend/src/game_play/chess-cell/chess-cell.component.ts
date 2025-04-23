import {Component, Input} from '@angular/core';
import {NgClass, NgIf} from '@angular/common';
import {Figure} from '../figure';
import {GamePlayService} from '../game-play-service';

@Component({
  selector: 'app-chess-cell',
  standalone: true,
  templateUrl: './chess-cell.component.html',
  styleUrls: ['./chess-cell.component.css'],
  imports: [NgIf, NgClass],
})
export class ChessCellComponent {
  @Input() id?: string; // Определяет, белая ли ячейка
  @Input() isWhite: boolean = false; // Определяет, белая ли ячейка
  figure: Figure | null = null
  isSelected: boolean = false; // Свойство для отслеживания состояния выбора

  constructor(private gamePlayService: GamePlayService) {
  }

  // Метод для получения класса ячейки
  getCellClass() {
    return {
      'cell': true, // всегда добавляем класс cell
      'selected': this.isSelected, // добавляем класс selected, если isSelected true
      'white': this.isWhite, // пример для других классов
      'black': !this.isWhite // пример для других классов
    };
  }
  select(isSelected: boolean) {
    this.isSelected = isSelected;
  }
}
