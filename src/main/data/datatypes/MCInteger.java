package main.data.datatypes;

public class MCInteger extends MCDatatype<Integer> {

  public MCInteger() {
    super(0);
  }
  
  public MCInteger(Integer value) {
    super(value);
  }
  
  public void add(Integer value) {
    this.value += value;
  }
}
