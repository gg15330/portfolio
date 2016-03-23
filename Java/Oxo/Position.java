public class Position {

  private int y;
  private int x;
  private Symbol sym;

  public Position(int y, int x, Symbol sym) {
    this.x = x;
    this.y = y;
    this.sym = sym;
  }

  public boolean checkFilled() {
    if(sym != Symbol.BLANK) { return true; }
    return false;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public Symbol getSym() {
    return this.sym;
  }

}
