//Handles the main running of the program -
//I began to implement a user interface with SQL-style
//commands, but ran out of time

//run "java Table" first to create a test table: dogs.tbl

import java.io.*;
import java.util.*;

public class Db {

  private Database database;
  private Filehandler filehandler;
  private IO io;
  private boolean running;

  public Db() {
    io = new IO();
    filehandler= new Filehandler();
    try { database = new Database(filehandler.loadTables(".")); }
    catch(Exception e) { database = new Database(); }
    running = true;
  }

  public static void main(String[] args) {
    if(args.length > 0) {
      System.out.println("Incorrect number of arguments.");
      System.exit(1);
    }
    Db program = new Db();
    program.run();
  }

  //Main program loop
  private void run() {
    io.welcome();
    String input = "";
    while(running) {
      io.commandPrompt();
      input = io.read();
      process(input.split(" "));
    }
  }

  //Processes user input and calls the relevant method
  private void process(String... words) {
    if(words.length == 0) { return; }
    try {
      switch(words[0]) {
        case "help": io.displayCommands(); break;
        case "add": add(words); break;
        case "load": break;
        case "save": break;
        case "select": break;
        case "list": break;
        case "print": print(words); break;
        case "exit": running = false; break;
        default: break;
      }
    }
    catch(Exception e) { System.out.println(e.getMessage()); }
  }

  //Adds either a Table or a Record, depending on user input
  private void add(String... words) throws Exception {
    if(words.length < 2) { throw new Exception("Usage:\nadd table\nadd record <tablename>"); }
    switch(words[1]) {
      case "table": addTable(words); break;
      case "record": addRecord(words); break;
      default: throw new Exception("Usage: add <\"table\"/\"record\">");
    }
  }

  //Adds a Table to the Database, prompting the user
  //for a table name and column names
  private void addTable(String... words) throws Exception {
    if(words.length != 2) { throw new Exception("Usage: add table"); }
    String name = io.read("tablename");
    String[] cols = io.read("columns").split(" ");
    Table t = new Table(name, cols);
    database.addTable(name, t);
  }

  //Adds a Record to a Table(Table is defined by the
  //second argument supplied by the user)
  private void addRecord(String... words) throws Exception {
    if(words.length < 3) { throw new Exception("Usage: add record <tablename>"); }
    if(!database.getTables().containsKey(words[2])) {
      throw new Exception("Table \"" + words[2] + "\" does not exist in database.");
    }
    String[] fields = io.read("fields").split(" ");
    Record r = new Record(database.getTable(words[2]).getTabledata(), fields);
    database.getTable(words[2]).addRecord(r);
  }

  //Prints a table - if the user chooses "all" as the second parameter,
  //all tables in the Database are printed
  private void print(String... words) throws Exception {
    if(words.length != 2) { throw new Exception("Usage: print <tablename>"); }
    switch(words[1]) {
      case "all":
        for(String s : database.getTables().keySet()) {
          io.print(database.getTables().get(s));
        }
        break;
      default:
        if(database.getTables().get(words[1]) == null) {
          throw new Exception("Table \"" + words[1] + "\" does not exist in database.");
        }
        io.print(database.getTables().get(words[1]));
    }
  }

}
