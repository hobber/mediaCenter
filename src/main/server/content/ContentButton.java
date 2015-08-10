package main.server.content;


public class ContentButton extends ContentItem {
  
  public ContentButton(int x, int y, String text, String callbackParameter) {
    super("button");
    setAttribute("x", x);
    setAttribute("y", y);   
    setAttribute("text", text);
    setAttribute("parameter", callbackParameter);    
  }
}
