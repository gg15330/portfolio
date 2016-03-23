//Holds data about the table including column names, number of columns
//and maximum column width(used for printing)

import java.util.*;

public class Tabledata implements java.io.Serializable{

  private String tablename;
  private Set<String> colnames = new LinkedHashSet<String>();
  private List<Integer> colwidths = new ArrayList<Integer>();

  public Tabledata() {}

  public Tabledata(String tablename) {
    this.tablename = tablename;
  }

  public Tabledata(String tablename, String... colnames) throws Exception {
    this.tablename = tablename;
    addCols(colnames);
  }

  //Adds a column - checks that the column name is not taken
  //then adds the corresponding column width to colwidths
  public void addCol(String colname) throws Exception {
    if(colnames.contains(colname)) {
      throw new Exception("Column names must be unique");
    }
    colnames.add(colname);
    colwidths.add(colname.length());
  }

  public void addCols(String[] colnames) throws Exception {
    for(String s : colnames) {
      addCol(s);
    }
  }

  //Deletes a column. Uses a temporary array to
  //find the index of the corresponding column width
  public void delCol(String colname) throws Exception {
    if(!colnames.contains(colname)) { throw new Exception("Column \"" + colname + "\" not found."); }
    List<String> temp = new ArrayList<String>(colnames);
    colwidths.remove(temp.indexOf(colname));
    colnames.remove(colname);
  }

  public void clearCols() {
    colnames.clear();
  }

  //Renames a column, checking that the old name exists
  //and the new name does not
  public void renameCol(String colname, String newname) throws Exception {
    if(!colnames.contains(colname)) {
      throw new Exception("Column \"" + colname + "\" not found.");
    }
    if(colnames.contains(newname)) {
      throw new Exception("Column \"" + newname + "\" already exists.");
    }
    List<String> temp = new ArrayList<String>(colnames);
    for(int i = 0; i < temp.size(); i++) {
      if(colname.equals(temp.get(i))) {
        temp.set(i, newname);
      }
    }
    colnames.clear();
    colnames.addAll(temp);
  }

  //updates a column width(called when changing
  // a column name to something longer than the original)
  public void setColWidth(int i, int n) {
    if(n <= 0) { throw new Error("Column width must be greater than 0"); }
    try { colwidths.set(i, n); }
    catch(Exception e) { throw new Error(e); }
  }

  public String getName() {
    return tablename;
  }

  //returns the colnames Set as a List of strings
  public List<String> getColNames() {
    return new ArrayList<String>(colnames);
  }

  public int getNumCols() {
    return colnames.size();
  }

  public List<Integer> getColWidths() {
    return colwidths;
  }

  public Tabledata get() {
    return this;
  }

  //Tests
  private static int tests;
  private static Tabledata testdata;
  private static List<String> expected;
  private static int[] expectedcolwidths;

  public static void main(String[] args) {
    setup();
    test_setColWidth();
    test_addCol();
    test_addCols();
    test_delCol();
    test_renameCol();
    test_clearCols();
    System.out.println("Tests passed: " + tests);
  }

  private static void setup() {
    tests = 0;
    try { testdata = new Tabledata("Testtable", "Name", "Age", "Hobbies"); }
    catch(Exception e) { throw new Error(e); }
    expected = new ArrayList<String>();
    expected.add("Name");
    expected.add("Age");
    expected.add("Hobbies");
    expectedcolwidths = new int[10];
    expectedcolwidths[0] = 4;
    expectedcolwidths[1] = 3;
    expectedcolwidths[2] = 7;
  }

  private static void test_setColWidth() {
    expectedcolwidths[0] = 10; test("setColWidth 0 10", expectedcolwidths[0]);
    expectedcolwidths[1] = 12; test("setColWidth 1 12", expectedcolwidths[1]);
    expectedcolwidths[0] = 4;
    expectedcolwidths[1] = 7;
    System.out.println(expected);
    System.out.println(testdata.getColNames());
  }

  private static void test_addCol() {
    expected.add("Occupation"); expectedcolwidths[3] = 10;
    test("addCol Occupation", expected);
    test("numCols", 4);
    is(testdata.getColWidths().get(3), expectedcolwidths[3]);
    expected.add("Favourite_food"); expectedcolwidths[4] = 14;
    test("addCol Favourite_food", expected);
    test("numCols", 5);
    is(testdata.getColWidths().get(4), expectedcolwidths[4]);
  }

  private static void test_addCols() {
    expected.add("Nickname"); expectedcolwidths[5] = 8;
    expected.add("Favourite_film"); expectedcolwidths[6] = 14;
    test("addCols Nickname Favourite_film", expected); test("numCols", 7);
    is(testdata.getColWidths().get(5), expectedcolwidths[5]);
    is(testdata.getColWidths().get(6), expectedcolwidths[6]);
  }

  private static void test_delCol() {
    expected.remove("Occupation");
    test("delCol Occupation", expected);
    test("numCols", 6);
    expected.remove("Hobbies"); test("delCol Hobbies", expected); test("numCols", 5);
  }

  private static void test_renameCol() {
    expected.set(0, "Whatsyaname"); test("renameCol Name Whatsyaname", expected);
    expected.set(2, "Bestnoms"); test("renameCol Favourite_food Bestnoms", expected);
  }

  private static void test_clearCols() {
    expected.clear(); test("clearCols", expected); test("numCols", 0);
  }

  private static void test(String in, Object out) {
    String[] words = in.split(" ");
    if (words[0].equals("addCol")) {
      try { testdata.addCol(words[1]); }
      catch(Exception e) { throw new Error(e); }
      is(testdata.getColNames(), out);
    }
    else if (words[0].equals("addCols")) {
      try { testdata.addCols(Arrays.copyOfRange(words, 1, words.length)); }
      catch(Exception e) { throw new Error(e); }
      is(testdata.getColNames(), out);
    }
    else if (words[0].equals("delCol")) {
      try {
        testdata.delCol(words[1]);
      }
      catch(Exception e) { throw new Error(e); }
      is(testdata.getColNames(), out);
    }
    else if (words[0].equals("renameCol")) {
      try {
        testdata.renameCol(words[1], words[2]);
      }
      catch(Exception e) { throw new Error(e); }
      is(testdata.getColNames(), out);
    }
    else if (words[0].equals("clearCols")) {
      testdata.clearCols();
      is(testdata.getColNames(), out);
    }
    else if (words[0].equals("numCols")) {
      is(testdata.getNumCols(), out);
    }
    else if (words[0].equals("setColWidth")) {
      testdata.setColWidth(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
      is(testdata.getColWidths().get(Integer.parseInt(words[1])), out);
    }
  }

  static void is(Object x, Object y) {
    tests++;
    if (x == y) return;
    if (x != null && x.equals(y)) return;
    throw new Error("Test failed: " + x + ", " + y);
  }
}
