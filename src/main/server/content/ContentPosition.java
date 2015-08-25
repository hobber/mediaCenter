package main.server.content;

public class ContentPosition {
  
  private int x;
  private int y;
  
  public ContentPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public String toString() {
    return "{x: " + x + ", y: " + y + "}";
  }
}
