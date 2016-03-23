//Handles saving/loading tables

import java.io.*;
import java.util.*;

public class Filehandler implements java.io.Serializable {

  //function adapted from http://www.tutorialspoint.com/java/java_serialization.htm
  //Saves a Table as a Serialized object,
  //throwing an exception if something goes wrong
  public ObjectOutputStream save(Table t) throws Exception {
    FileOutputStream fos = new FileOutputStream(t.getName() + ".tbl");
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(t);
    oos.flush();
    oos.close();
    return oos;
  }

  //Loads all .tbl files in the current working Directory into the database
  public Map<String, Table> loadTables(String foldername) throws Exception {
    File folder = new File(foldername);
    if(folder.isDirectory()) {
      Map<String, Table> tables = new HashMap<String, Table>();
      for(File f : folder.listFiles()) {
        if(f.getName().endsWith(".tbl")) {
          Table t = open(f.getName());
          tables.put(createTableNameFromFile(f), t);
        }
      }
      return tables;
    }
    throw new Exception("Could not load .tbl files.");
  }

  //converts a .tbl file into a Table object
  private Table open(String filename) {
    Table o = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      ObjectInputStream ois = new ObjectInputStream(fis);
      o = (Table)ois.readObject();
      ois.close();
    }
    catch(Exception e) { System.out.println("Could not open Table: " + filename); }
    return o;
  }

  public String createTableNameFromFile(File f) {
    int dot = f.getName().lastIndexOf(".");
    return f.getName().substring(0, dot);
  }

}
