package main.data.datatypes;

import java.util.LinkedList;
import java.util.List;

public class MCList<Type> extends MCDatatype<List<Type>> {

  public MCList() {
    super(new LinkedList<Type>());
  }
  
  public MCList(List<Type> value) {
    super(value);
  }
  
  public boolean add(Type value) {
    return this.value.add(value);
  }
  
  public void clear() {
    value.clear();
  }
  
  public int size() {
    return value.size();
  }
}
