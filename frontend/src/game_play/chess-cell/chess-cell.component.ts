import {Component, Input} from '@angular/core';
import {NgClass, NgIf} from '@angular/common';
import {Figure} from '../figure';

@Component({
  selector: 'app-chess-cell',
  standalone: true,
  templateUrl: './chess-cell.component.html',
  styleUrls: ['./chess-cell.component.css'],
  imports: [NgIf, NgClass],
})
export class ChessCellComponent {
  @Input() id!: string; // Определяет, белая ли ячейка
  @Input() isWhite: boolean = false; // Определяет, белая ли ячейка
  figure: Figure | null = null
  isSelected: boolean = false; // Свойство для отслеживания состояния выбора
  isMove: boolean = false;
  isEat: boolean = false;
  isSwap: boolean = false;

  // Метод для получения класса ячейки
  getCellClass() {
    return {
      'cell': true, // всегда добавляем класс cell
      'selected': this.isSelected, // добавляем класс selected, если isSelected true
      'move': this.isMove,
      'eat': this.isEat,
      'swap': this.isSwap,
      'white': this.isWhite, // пример для других классов
      'black': !this.isWhite // пример для других классов
    };
  }
  clear(){
    this.isSelected = false;
    this.isMove = false;
    this.isEat = false;
    this.isSwap = false;
  }
}
