class Oxo {

  private int BOARDSIZE;
  private int NUMPLAYERS;
  private boolean running;
  private Display display;
  private Board board;

  public static void main(String[] args) {
    Oxo oxo = new Oxo();
    //oxo.setBoardSize(args);
    oxo.setup();
    //oxo.runTests();
    oxo.runGame();
  }

  //I was aiming to make the board expandable but ran out of time
/*  private void setBoardSize(String[] args) {
    if(args.length == 0) {
      System.out.println("HI");
      BOARDSIZE = 5;
      return; }
    try {
      if(args.length >= 1) { BOARDSIZE = Integer.parseInt(args[0]) + 2; }
    }
    catch(Exception e) { fail(); }
  }*/

  private void fail() {
    System.err.println("Use: java Oxo [size]");
    System.exit(1);
  }

  private void setup() {
    BOARDSIZE = 5;
    NUMPLAYERS = (Symbol.values().length) - 1;
    board = new Board(BOARDSIZE);
    display = new Display(board);
  }

  private void runTests() {
    int tests = 0;
    tests += board.test();
    System.out.println("Tests passed: " + tests);
  }

  private void runGame() {
    running = true;
    int player = 1;
    boolean turnsuccess = false;
    while(running) {
      System.out.println("Player " + player + "'s turn");
      do {
        turnsuccess = turn(player);
      } while(turnsuccess == false);
      try {
        display.printBoard();
        checkGameOver(player);
        player++;
        if(player > NUMPLAYERS) { player = 1; }
      }
      catch(Exception e) {
        throw new Error(e);
      }
    }
  }

  //Player turn - take input and update the board
  private boolean turn(int player) {
    System.out.print("Enter some coordinates:\n");

    try {
      display.playerInput(player);
    }
    catch(Exception e) {
      System.out.print("\nIncorrect input. Use: [a][1]\n");
      return false;
    }
    int y = display.getY();
    int x = display.getX();
    Position p = new Position(y, x, playerSymbol(player));
    try {
      board.update(p);
    }
    catch (Exception e) {
      System.out.println("\nInvalid move. Please select another position:\n");
      return false;
    }
    return true;
  }

  //Check for win or draw states
  private void checkGameOver(int player) {
    if((board.checkWin() == true)) {
      System.out.println("Player " + player + " wins!");
      running = false;
    }
    else if(board.filled()) {
      System.out.println("Draw!");
      running = false;
    }
  }

  //Get corresponding symbol from player number
  private Symbol playerSymbol(int player) {
    return Symbol.values()[player - 1];
  }

}
