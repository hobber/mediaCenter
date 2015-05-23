package main.data.datatypes;

public class MCString extends MCDatatype<String> {

  public MCString() {
    super("");
  }
  
  public MCString(String value) {
    super(value);
  }
  
  public int length() {
    return value.length();
  }
  
  @Override
  public String toString() {
    return new String(value);
  }
}
