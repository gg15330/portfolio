//move setColWidths to Tabledata class
//setColWidths takes List instead of array?

import java.util.*;

public class Record implements java.io.Serializable {

  private List<String> row = new ArrayList<String>();
  private Tabledata tabledata;
  private int rowid = 0;

  //Constructor checks that number of fields being passed
  //corresponds to the number of columns in the table
  public Record(Tabledata tabledata, String[] vals) throws Exception {
    this.tabledata = tabledata;
    if(vals.length != tabledata.getNumCols()) {
      throw new Exception("Number of fields must match number of columns in table.");
    }
    for(int i = 0; i < vals.length; i++) {
      addField(vals[i]);
    }
    setColWidths(vals);
  }

  //In theory this function will never be called,
  //as the user will only create a whole Record at a time
  //rather than adding fields one by one
  private void addField(String value) throws Exception {
    if(row.size() >= tabledata.getNumCols()) {
      throw new Exception("Record full");
    }
    row.add(value);
  }

  //changes the value of a field
  //note: I have not added a check to expand the relevant column yet
  public void setField(int index, String value) throws Exception {
    if(row.size() == 0) {
      throw new Exception("Record empty");
    }
    if(index >= row.size()) {
      throw new IndexOutOfBoundsException("Record index must not exceed size of record");
    }
    if(index < 0) {
      throw new IndexOutOfBoundsException("Record index must be greater than 0");
    }
    row.set(index, value);
  }

  //Deletes a field by replacing it with null
  public void delField(int index) throws Exception {
    if(row.size() == 0) { throw new Exception("Record empty"); }
    if(index >= row.size()) { throw new Exception("Record index must not exceed size of record"); }
    setField(index, null);
  }

  //Updates column widths, replacing old values
  //if the new values are larger (useful for printing tables)
  public void setColWidths(String... vals) {
    int oldwidth = 0;
    int newwidth = 0;
    for(int i = 0; i < tabledata.getNumCols(); i++) {
      oldwidth = tabledata.getColWidths().get(i);
      newwidth = vals[i].length();
      if(newwidth > oldwidth) {
        tabledata.setColWidth(i, newwidth);
      }
    }
  }

  public String getField(String col) {
    return row.get(tabledata.getColNames().indexOf(col));
  }

  public List getFields() {
    return this.row;
  }

  public int getSize() {
    return row.size();
  }

  public Record get() {
    return this;
  }

  public void print(Integer... colwidths) {
    if(colwidths.length != getSize()) {
      throw new Error("Cannot print Record: column widths given do not match number of fields.");
    }
    for(int i = 0; i < getSize(); i++) {
      System.out.format("%" + ((colwidths[i] + 1) * -1) + "s", row.get(i));
    }
    System.out.println();
  }

//Tests
  private static int tests;
  private static Record testrecord;
  private static Tabledata testdata;
  private static List<String> expected;
  private static int[] expectedcolwidths;

  public static void main(String[] args) {
    setup();
    test_constructor();
    test_delField();
    test_setField();
    test_setColWidths();
    System.out.println("Tests passed: " + tests);
  }

  private static void setup() {
    tests = 0;
    testdata = new Tabledata();
    try {
      testdata.addCol("Name");
      testdata.addCol("Age");
      testdata.addCol("Hobbies");
    }
    catch(Exception e) { throw new Error(e); }

    expected = new ArrayList<String>();
    expected.add("Potato");
    expected.add("2");
    expected.add("woofing");
    expectedcolwidths = new int[10];
    expectedcolwidths[0] = 4;
    expectedcolwidths[1] = 1;
    expectedcolwidths[2] = 7;
  }

  private static void test_constructor() {
    String[] testvals = { "Potato", "2", "woofing" };
    try { testrecord = new Record(testdata, testvals); }
    catch(Exception e) { throw new Error(e); }
  }

  private static void test_delField() {
    expected.set(0, null); test("delField 0", expected);
    expected.set(2, null); test("delField 2", expected);
  }

  private static void test_setField() {
    expected.set(0, "Asparagus"); test("setField 0 Asparagus", expected);
    expected.set(1, "12345"); test("setField 1 12345", expected);
    expected.set(2, "woofing"); test("setField 2 woofing", expected);
  }

  private static void test_setColWidths() {
    expectedcolwidths[0] = 9; test("setColWidth 0", expectedcolwidths[0]);
    expectedcolwidths[1] = 5; test("setColWidth 1", expectedcolwidths[1]);
  }

  private static void test(String in, Object out) {
    String[] words = in.split(" ");
    if (words[0].equals("addField")) {
      try { testrecord.addField(words[1]); is(testrecord.getFields(), out); }
      catch(Exception e) { throw new Error(e); }
    }
    else if (words[0].equals("delField")) {
      try {
        testrecord.delField(Integer.parseInt(words[1]));
        is(testrecord.getFields(), out);
      }
      catch(Exception e) { throw new Error(e); }
    }
    else if (words[0].equals("setField")) {
      try {
        testrecord.setField(Integer.parseInt(words[1]), words[2]);
        is(testrecord.getFields(), out);
      }
      catch(Exception e) { throw new Error(e); }
    }
    else if (words[0].equals("setColWidth")) {
      testrecord.setColWidths(expected.toArray(new String[10]));
      is(testdata.getColWidths().get(Integer.parseInt(words[1])), out);
    }
  }

  private static void is(Object x, Object y) {
    tests++;
    if (x == y) return;
    if (x != null && x.equals(y)) return;
    throw new Error("Test failed: " + x + ", " + y);
  }
}
