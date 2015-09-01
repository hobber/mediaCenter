package main.utils;

public class Timestamp {

  private long timestamp;
  
  public Timestamp() {
    timestamp = System.currentTimeMillis();
  }
  
  public Timestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  public long value() {
    return timestamp;
  }
}
