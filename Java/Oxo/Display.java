import java.util.Scanner;
import java.lang.Character;

public class Display {

  private int tests;
  private final int INPUTLENGTH = 2;
  private final int CHAROFFSET = 9;
  private int coordY;
  private int coordX;
  private Scanner scanner;
  private Board board;
  private Position position;

  public Display(Board board) {
    this.tests = 0;
    this.scanner = new Scanner(System.in);
    this.board = board;
    this.coordY = 0;
    this.coordX = 0;
  }

  //Scan input from player and convert it to a Position object
  public void playerInput(int player) throws Exception{
    String input = scanner.next();
    convertInputToCoords(input, player);
  }

  //Converts string input to coordinates and checks they are within range
  private void convertInputToCoords(String s, int player) throws Exception {
    checkvalidInput(s);
    coordY = (Character.getNumericValue(s.charAt(0))) - CHAROFFSET;
    coordX = Integer.parseInt(Character.toString(s.charAt(1)));
    checkRange(coordY, coordX);
  }

  //Input must be in the form [a][1]
  private void checkvalidInput(String s) throws Exception {
    if((s.length() == 2)
    && (Character.isLetter(s.charAt(0))
    && (Character.isDigit(s.charAt(1))))) { return; }
    throw new Exception();
  }

  private void checkRange(int y, int x) throws Exception {
    int boardsize = board.getBoardSize();
    if(((y <= 0) || (y > boardsize - 2))
    || ((x <= 0) || (x > boardsize - 2))) {
      throw new Exception();
    }
  }

  public void printBoard() {
    System.out.print("\n  ");
    for(int i = 1; i < board.getBoardSize() - 1; i++) {
      System.out.format("%d", i);
    }
    System.out.print("\n\n");
    for(int y = 1; y < board.getBoardSize() - 1; y++) {
      System.out.format("%c ", ('a'+ y - 1));
      for(int x = 1; x < board.getBoardSize() - 1; x++) {
        Position p = board.getPos(y, x);
        if(p.getSym() == Symbol.BLANK) { System.out.print("."); }
        else  { System.out.print(board.getPos(y, x).getSym()); }
      }
      System.out.println();
    }
    System.out.println();
  }

  public Position getPos() {
    return this.position;
  }

  public int getY() {
    return this.coordY;
  }

  public int getX() {
    return this.coordX;
  }

}
