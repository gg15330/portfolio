import java.util.*;

public class Board {

  private int tests;
  private Position[][] b;
  private int BOARDSIZE;

  public Board(int size) {
    this.tests =0;
    this.BOARDSIZE = size;
    initBoard();
  }

  //Set board filled with blank positions
  private void initBoard() {
    b = new Position[BOARDSIZE][BOARDSIZE];
    for(int y = 0; y < BOARDSIZE; y++) {
      for(int x = 0; x < BOARDSIZE; x++) {
        b[y][x] = new Position(y, x, Symbol.BLANK);
      }
    }
  }

  //Updates board with new position object,
  //throws exception if cell is filled
  public void update(Position p) throws Exception {
    int y = p.getY();
    int x = p.getX();
    if(b[y][x].checkFilled()) { throw new Exception(); }
    b[y][x] = p;
  }

  //Loops through board and checks surrounding cells for 3 in a row
  public boolean checkWin() {
    for(int y = 1; y < BOARDSIZE - 1; y++) {
      for(int x = 1; x < BOARDSIZE - 1; x++) {
        if(threeInARow(b[y][x], b[y-1][x], b[y+1][x])) { return true; }
        else if(threeInARow(b[y][x], b[y][x-1], b[y][x+1])) { return true; }
        else if(threeInARow(b[y][x], b[y-1][x-1], b[y+1][x+1])) { return true; }
        else if(threeInARow(b[y][x], b[y+1][x-1], b[y-1][x+1])) { return true; }
      }
    }
    return false;
  }

  //Is the board full?
  public boolean filled() {
    for(int y = 1; y < BOARDSIZE - 1; y++) {
      for(int x = 1; x < BOARDSIZE - 1; x++) {
        if(b[y][x].checkFilled() == false) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean threeInARow(Position p1, Position p2, Position p3) {
    if(p1.getSym() == Symbol.BLANK) { return false; }
    else {
      if((p1.getSym() == p2.getSym())
      && (p1.getSym() == p3.getSym())
      && (p2.getSym() == p3.getSym())) { return true; }

      return false;
    }
  }

  public int getBoardSize() {
    return this.BOARDSIZE;
  }

  public Position getPos(int y, int x) {
    return b[y][x];
  }

  public void is(Object x, Object y) {
    tests++;
    if (x == y) return;
    if (x != null && x.equals(y)) return;
    throw new Error("Test " + tests + " failed: " + x + ", " + y);
  }

  public int test() {
    System.out.println("Testing Board...");

    this.BOARDSIZE = 0; is(getBoardSize(), 0);
    this.BOARDSIZE = 3; is(getBoardSize(), 3);
    this.BOARDSIZE = 10; is(getBoardSize(), 10);
    this.BOARDSIZE = 106585; is(getBoardSize(), 106585);
    this.BOARDSIZE = 5;

    Position pos = new Position(1, 1, Symbol.X);
    b[1][1] = pos; is(getPos(1, 1), pos);
    pos = new Position(3, 3, Symbol.O);
    b[3][3] = pos; is(getPos(3, 3), pos);

    b[1][1] = new Position(1, 1, Symbol.O);
    b[1][2] = new Position(1, 2, Symbol.O);
    b[1][3] = new Position(1, 3, Symbol.O);
    is(checkWin(), true);
    is(threeInARow(b[1][1], b[1][2], b[1][3]), true);
    b[1][1] = new Position(1, 1, Symbol.X);
    b[2][1] = new Position(2, 1, Symbol.X);
    b[3][1] = new Position(3, 1, Symbol.X);
    is(checkWin(), true);
    is(threeInARow(b[1][1], b[2][1], b[3][1]), true);
    b[1][1] = new Position(1, 1, Symbol.O);
    b[2][2] = new Position(2, 2, Symbol.O);
    b[3][3] = new Position(3, 3, Symbol.O);
    is(checkWin(), true);
    is(threeInARow(b[1][1], b[2][2], b[3][3]), true);
    b[1][1] = new Position(1, 1, Symbol.BLANK);
    b[1][2] = new Position(1, 2, Symbol.BLANK);
    b[1][3] = new Position(1, 3, Symbol.BLANK);
    is(checkWin(), false);
    is(threeInARow(b[1][1], b[1][2], b[1][3]), false);

    for(int y = 1; y < BOARDSIZE - 1; y++) {
      for(int x = 1; x < BOARDSIZE - 1; x++) {
        b[y][x] = new Position(y, x, Symbol.X);
      }
    }
    is(filled(), true);
    for(int y = 1; y < BOARDSIZE - 1; y++) {
      for(int x = 1; x < BOARDSIZE - 1; x++) {
        b[y][x] = new Position(y, x, Symbol.BLANK);
      }
    }
    is(filled(), false);
    return tests;
  }
}
