//Handles reading input/displaying messages to the user

import java.io.*;
import java.util.*;

public class IO {

  private Scanner scanner;
  private String[] commands = { "help", "add", "print", "exit" };

  public IO() {
    scanner = new Scanner(System.in);
  }

  public void welcome() {
    System.out.println("Welcome to the database.");
  }

  public void commandPrompt() {
    System.out.println("Enter a command, or type 'help' for a list of commands:");
  }

  public String read() {
    System.out.print(">");
    return scanner.nextLine();
  }

  //Prompts the user for different input depending on the operation
  public String read(String request) {
    switch(request) {
      case "tablename": System.out.println("Enter table name:"); break;
      case "columns": System.out.println("Enter column names, separated with a space:"); break;
      case "fields": System.out.println("Enter field values, separated with a space:"); break;
      default: break;
    }
    System.out.print(">");
    return scanner.nextLine();
  }

  //Displays available commands
  public void displayCommands() {
    System.out.println("\nCommands:");
    for(String s : commands) { System.out.println(s); }
    System.out.println();
  }

  //I was intending to refactor the print method
  //in the Table class/move it to this class but ran out of time
  public void print(Table t) {
    t.print(t.getColWidths());
  }

}
