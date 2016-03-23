import java.io.*;
import java.util.*;

public class Table implements java.io.Serializable {
  private String tablename;
  private int rowid;
  private Map<Integer, Record> records = new HashMap<Integer, Record>();
  private Set<Integer> keys = new TreeSet<Integer>();
  private Tabledata tabledata;

  public Table(String tablename) {
    rowid = 0;
    tabledata = new Tabledata(tablename);
  }

  public Table(String tablename, String... colnames) throws Exception {
    this.tablename = tablename;
    rowid = 0;
    tabledata = new Tabledata(tablename, colnames);
  }

  //Adds a record to the table
  //Checks rowid (should always be unique - hence the Error)
  //Uses a temporary array to update column widths
  public void addRecord(Record r) {
    if(records.containsKey(rowid)) { throw new Error("Duplicate row id: " + rowid); }
    records.put(rowid++, r);
    String[] temp = new String[tabledata.getNumCols()];
    for(int i = 0; i < tabledata.getNumCols(); i++) {
      temp[i] = r.getField(tabledata.getColNames().get(i));
    }
    r.setColWidths(temp);
  }

  //deletes a record by rowid
  public void delRecord(Integer i) throws Exception {
    if(!records.containsKey(i)) { throw new Exception("Record not found."); }
    records.remove(i);
  }

  //renames a column
  public void renameCol(String colname, String newname) throws Exception {
    tabledata.renameCol(colname, newname);
  }

  public List<String> getColNames() {
    return tabledata.getColNames();
  }

  //requests a record by rowid, checking that
  //the row id is not greater than the number of records
  public Record getRecord(int row) throws Exception{
    if(records.size() <= row) {
      throw new Exception("Maximum rows exceeded. Row id: " + row + ". Number of records: " + records.size());
    }
    else if(row < 0) {
      throw new Exception("Cannot request negative row number. Obviously.");
    }
    return records.get(row);
  }

  public Map<Integer, Record> getRecords() {
    return records;
  }

  public int getNumOfRecords() {
    return records.size();
  }

  public String getName() {
    return tabledata.getName();
  }

  //Returns columns widths as an array
  public Integer[] getColWidths() {
    return tabledata.getColWidths().toArray(new Integer[tabledata.getNumCols()]);
  }

  public Tabledata getTabledata() {
    return tabledata;
  }

  public void print(Integer... colwidths) {
    System.out.println("Table: " + getName());
    if(colwidths.length != tabledata.getNumCols()) {
      throw new Error("Cannot print Table: column widths given do not match number of fields.");
    }

    int rowidcolwidth = 7;
    int totalwidth = 0;
    //print column names
    System.out.format("%-" + rowidcolwidth + "s", "");
    for(int i = 0; i < tabledata.getNumCols(); i++) {
      System.out.format("%" + ((colwidths[i] + 1) * -1) + "s", tabledata.getColNames().get(i));
      totalwidth += (colwidths[i] + 1);
    }
    System.out.println();

    //dashed line
    for(int i = 0; i < (rowidcolwidth + totalwidth - 1); i++) {
      System.out.print("-");
    }
    System.out.println();

    //rows
    for(Integer l : records.keySet()) {
      System.out.format("%-" + rowidcolwidth + "s", l);
      records.get(l).print(colwidths);
    }
    System.out.format("\n");
  }

  //Tests
  private static int tests;
  private static Table testtable;
  private static Map<String, Record> testrecords;
  private static Record tr1;
  private static Record tr2;
  private static Record tr3;
  private static List<String> expectedcolnames;
  private static Map<Integer, Record> expectedrecords;
  private static int[] expectedcolwidths;

  public static void main(String[] args) {
    setup();
    test_addRecord();
    test_getRecord();

    try {
      Filehandler fh = new Filehandler();
      fh.save(testtable);
    }
    catch(Exception e) { throw new Error(e); }

    test_delRecord();
    test_renameCol();
    System.out.println("Tests passed: " + tests);
  }

  private static void setup() {
    tests = 0;
    String[] testcols = { "Name", "Age", "Hobbies" };
    try { testtable = new Table("dogs", testcols); }
    catch(Exception e) {throw new Error(e); }

    String[] testvals1 = { "Potato", "2", "woofing" };
    String[] testvals2 = { "Asparagus", "5", "barking" };
    String[] testvals3 = { "Fido", "7", "chartered accountancy" };
    try {
      tr1 = new Record(testtable.getTabledata(), testvals1);
      tr2 = new Record(testtable.getTabledata(), testvals2);
      tr3 = new Record(testtable.getTabledata(), testvals3);
    }
    catch(Exception e) { throw new Error(e); }

    testrecords = new HashMap<String, Record>();
    testrecords.put("tr1", tr1);
    testrecords.put("tr2", tr2);
    testrecords.put("tr3", tr3);

    expectedrecords = new HashMap<Integer, Record>();
    expectedcolnames = new ArrayList<String>();
    expectedcolnames.add(testcols[0]);
    expectedcolnames.add(testcols[1]);
    expectedcolnames.add(testcols[2]);
    expectedcolwidths = new int[3];
    expectedcolwidths[0] = 9;
    expectedcolwidths[1] = 3;
    expectedcolwidths[2] = 21;
  }

  private static void test_addRecord() {
    expectedrecords.put(0, tr1); test("addRecord tr1", expectedrecords);
    is(testtable.getTabledata().getColWidths().get(0), expectedcolwidths[0]);
    expectedrecords.put(1, tr2); test("addRecord tr2", expectedrecords);
    is(testtable.getTabledata().getColWidths().get(1), expectedcolwidths[1]);
    expectedrecords.put(2, tr3); test("addRecord tr3", expectedrecords);
    is(testtable.getTabledata().getColWidths().get(2), expectedcolwidths[2]);
    is(testtable.getRecords(), expectedrecords);
  }

  private static void test_getRecord() {
    test("getRecord 0", tr1);
    test("getRecord 1", tr2);
    test("getRecord 2", tr3);
  }

  //test for new column widths
  private static void test_delRecord() {
    expectedrecords.remove(0); test("delRecord 0", expectedrecords);
    expectedrecords.remove(2); test("delRecord 2", expectedrecords);
    is(expectedrecords, testtable.getRecords());
  }

  private static void test_renameCol() {
    expectedcolnames.set(0, "Myname"); test("renameCol Name Myname", expectedcolnames);
    expectedcolnames.set(2, "Interests"); test("renameCol Hobbies Interests", expectedcolnames);
    is(expectedrecords, testtable.getRecords());
  }

  private static void test(String in, Object out) {
    String[] words = in.split(" ");
    if (words[0].equals("addRecord")) {
      testtable.addRecord(testrecords.get(words[1]));
      is(testtable.getRecords(), out);
    }
    else if (words[0].equals("delRecord")) {
      try {
        testtable.delRecord(Integer.parseInt(words[1]));
        is(testtable.getRecords(), out);
      }
      catch(Exception e) { throw new Error(e); }
    }
    else if (words[0].equals("renameCol")) {
      try {
        testtable.renameCol(words[1], words[2]);
        is(testtable.getColNames(), out);
      }
      catch(Exception e) { throw new Error(e); }
    }
    else if (words[0].equals("getRecord")) {
      try {
        is(testtable.getRecord(Integer.parseInt(words[1])), out);
      }
      catch(Exception e) { throw new Error(e); }
    }
  }

  private static void is(Object x, Object y) {
    tests++;
    if (x == y) return;
    if (x != null && x.equals(y)) return;
    throw new Error("Test failed: " + x + ", " + y);
  }
}
