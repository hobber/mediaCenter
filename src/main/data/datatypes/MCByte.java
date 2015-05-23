package main.data.datatypes;

public class MCByte extends MCDatatype<Byte> {

  public MCByte() {
    super((byte)0);
  }
  
  public MCByte(Byte value) {
    super(value);
  }
}
