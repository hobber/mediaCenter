package main.data.datatypes;

public abstract class MCDatatype<Type> {
  
  protected Type value;

  public MCDatatype(Type value) {    
    this.value = value;
  }
  
  public Type get() {
    return value;
  }
  
  public void set(Type value) {
    this.value = value;
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
