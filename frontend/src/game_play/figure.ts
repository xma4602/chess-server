import {FigureColor} from '../game_conditions/game-conditions';

export class Figure {
  // Приватные поля
  private constructor(
    public readonly code: string,
    public readonly notationChar: string,
    public readonly color: FigureColor,
    public readonly icon: string
  ) {
  }

  // Статические экземпляры фигур
  static readonly B_KING = new Figure('BLACK_KING', 'K', FigureColor.BLACK, 'assets/chess/b_king.png');
  static readonly W_KING = new Figure('WHITE_KING', 'K', FigureColor.WHITE, 'assets/chess/w_king.png');
  static readonly B_QUEEN = new Figure('BLACK_QUEEN', 'Q', FigureColor.BLACK, 'assets/chess/b_queen.png');
  static readonly W_QUEEN = new Figure('WHITE_QUEEN', 'Q', FigureColor.WHITE, 'assets/chess/w_queen.png');
  static readonly B_BISHOP = new Figure('BLACK_BISHOP', 'B', FigureColor.BLACK, 'assets/chess/b_bishop.png');
  static readonly W_BISHOP = new Figure('WHITE_BISHOP', 'B', FigureColor.WHITE, 'assets/chess/w_bishop.png');
  static readonly B_KNIGHT = new Figure('BLACK_KNIGHT', 'N', FigureColor.BLACK, 'assets/chess/b_knight.png');
  static readonly W_KNIGHT = new Figure('WHITE_KNIGHT', 'N', FigureColor.WHITE, 'assets/chess/w_knight.png');
  static readonly B_ROOK = new Figure('BLACK_ROOK', 'R', FigureColor.BLACK, 'assets/chess/b_rook.png');
  static readonly W_ROOK = new Figure('WHITE_ROOK', 'R', FigureColor.WHITE, 'assets/chess/w_rook.png');
  static readonly B_PAWN = new Figure('BLACK_PAWN', 'P', FigureColor.BLACK, 'assets/chess/b_pawn.png');
  static readonly W_PAWN = new Figure('WHITE_PAWN', 'P', FigureColor.WHITE, 'assets/chess/w_pawn.png');

  // Метод для получения всех фигур
  static getAllFigures() {
    return [
      Figure.B_KING,
      Figure.W_KING,
      Figure.B_QUEEN,
      Figure.W_QUEEN,
      Figure.B_BISHOP,
      Figure.W_BISHOP,
      Figure.B_KNIGHT,
      Figure.W_KNIGHT,
      Figure.B_ROOK,
      Figure.W_ROOK,
      Figure.B_PAWN,
      Figure.W_PAWN,
    ];
  }
}
