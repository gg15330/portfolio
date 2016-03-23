//Holds all Tables in a Map<String, Table>
//where String is the Table name

import java.io.*;
import java.util.*;

public class Database implements java.io.Serializable {

  public Map<String, Table> tables;

  public Database() {
    this.tables = new HashMap<String, Table>();
  }

  public Database(Map<String, Table> tables) {
    this.tables = tables;
  }

  public Map<String, Table> getTables() {
    return this.tables;
  }

  public Table getTable(String name) {
    return tables.get(name);
  }

  //Adds a table to the database, checks that the
  //table name does not already exist
  public void addTable(String s, Table t) throws Exception {
    if(tables.containsKey(s)) {
      throw new Exception("Cannot add table - non-unique table name: " + s);
    }
    tables.put(s, t);
  }

}
