package main.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class FileWriter {

  private String fileName;
  private FileOutputStream stream;
  private byte[] tmpByte = new byte[1];
  private byte[] tmpShort = new byte[2];
  private byte[] tmpInt = new byte[4];
  private byte[] tmpLong = new byte[8];
  
  public FileWriter(String fileName) throws FileNotFoundException {
    this.fileName = fileName;
    stream = new FileOutputStream(fileName);    
  }
  
  public void close() throws IOException {
    if(stream != null)
      stream.close();
    stream = null;
  }
  
  public void writeByte(byte value) throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    tmpByte[0] = value;
    stream.write(tmpByte);
  }
  
  public void writeShort(short value) throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    tmpShort[0] = (byte) (value       & 0xff);
    tmpShort[1] = (byte)((value >> 8) & 0xff);
    stream.write(tmpShort);
  }
  
  public void writeInt(int value) throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    tmpInt[0] = (byte) (value        & 0xff);
    tmpInt[1] = (byte)((value >>  8) & 0xff);
    tmpInt[2] = (byte)((value >> 16) & 0xff);
    tmpInt[3] = (byte)((value >> 24) & 0xff);
    stream.write(tmpInt);
  }
  
  public void writeLong(long value) throws IOException {
    if(stream == null)
      throw new IOException("file " + fileName + " already closed");
    tmpLong[0] = (byte) (value        & 0xff);
    tmpLong[1] = (byte)((value >>  8) & 0xff);
    tmpLong[2] = (byte)((value >> 16) & 0xff);
    tmpLong[3] = (byte)((value >> 24) & 0xff);
    tmpLong[4] = (byte)((value >> 32) & 0xff);
    tmpLong[5] = (byte)((value >> 40) & 0xff);
    tmpLong[6] = (byte)((value >> 48) & 0xff);
    tmpLong[7] = (byte)((value >> 56) & 0xff);
    stream.write(tmpLong);
  }
  
  public void writeFloat(float value) throws IOException {
    writeInt(Float.floatToRawIntBits(value));
  }
  
  public void writeDouble(double value) throws IOException {
    writeLong(Double.doubleToRawLongBits(value));
  }
  
  public void writeString(String value) throws IOException {
    stream.write(value.getBytes());
  }
  
  public void writeTime(Calendar calendar) throws IOException {
    writeLong(calendar.getTimeInMillis());
  }
}
