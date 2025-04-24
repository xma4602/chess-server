export class Figure {
  // Приватные поля
  private constructor(
    public readonly name: string,
    public readonly color: string,
    public readonly icon: string
  ) {
  }

  // Статические экземпляры фигур
  static readonly B_KING = new Figure('king', 'black', 'assets/chess/b_king.png');
  static readonly W_KING = new Figure('king', 'white', 'assets/chess/w_king.png');
  static readonly B_QUEEN = new Figure('queen', 'black', 'assets/chess/b_queen.png');
  static readonly W_QUEEN = new Figure('queen', 'white', 'assets/chess/w_queen.png');
  static readonly B_BISHOP = new Figure('bishop', 'black', 'assets/chess/b_bishop.png');
  static readonly W_BISHOP = new Figure('bishop', 'white', 'assets/chess/w_bishop.png');
  static readonly B_KNIGHT = new Figure('knight', 'black', 'assets/chess/b_knight.png');
  static readonly W_KNIGHT = new Figure('knight', 'white', 'assets/chess/w_knight.png');
  static readonly B_ROOK = new Figure('rook', 'black', 'assets/chess/b_rook.png');
  static readonly W_ROOK = new Figure('rook', 'white', 'assets/chess/w_rook.png');
  static readonly B_PAWN = new Figure('pawn', 'black', 'assets/chess/b_pawn.png');
  static readonly W_PAWN = new Figure('pawn', 'white', 'assets/chess/w_pawn.png');

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
