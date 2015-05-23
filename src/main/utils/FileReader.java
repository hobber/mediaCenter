package main.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReader {

  private String fileName;
  private FileInputStream stream;
  private byte[] tmpByte = new byte[1];
  private byte[] tmpShort = new byte[2];
  private byte[] tmpInt = new byte[4];
  private byte[] tmpLong = new byte[8];
  
  public FileReader(String fileName) throws FileNotFoundException {
    this.fileName = fileName;
    stream = new FileInputStream(fileName);    
  }
  
  public void close() throws IOException {
    if(stream != null)
      stream.close();
    stream = null;
  }
  
  public byte readByte() throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    stream.read(tmpByte);
    return tmpByte[0];
  }
  
  public short readShort() throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    stream.read(tmpShort);
    return (short)(((short)tmpShort[1] << 8) + tmpShort[0]);
  }
  
  public int readInt() throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    stream.read(tmpInt);
    return shiftByteToInt(tmpInt[0],  0) + 
           shiftByteToInt(tmpInt[1],  8) + 
           shiftByteToInt(tmpInt[2], 16) + 
           shiftByteToInt(tmpInt[3], 24);
  }
  
  public long readLong() throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    stream.read(tmpLong);
    return shiftByteToLong(tmpLong[0],  0) + 
           shiftByteToLong(tmpLong[1],  8) + 
           shiftByteToLong(tmpLong[2], 16) + 
           shiftByteToLong(tmpLong[3], 24) +
           shiftByteToLong(tmpLong[4], 32) + 
           shiftByteToLong(tmpLong[5], 40) + 
           shiftByteToLong(tmpLong[6], 48) + 
           shiftByteToLong(tmpLong[7], 56);    
  }
  
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }
  
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }
  
  public String readString(int length) throws IOException {
    byte[] buffer = new byte[length];
    stream.read(buffer);
    return new String(buffer);
  }
  
  private int shiftByteToInt(byte value, int shiftValue) {
    if(value < 0)
      return ((int)value + 256) << shiftValue;
    return (int)value << shiftValue;
  }
  
  private long shiftByteToLong(byte value, int shiftValue) {
    if(value < 0)
      return ((long)value + 256) << shiftValue;
    return (long)value << shiftValue;
  }
}
