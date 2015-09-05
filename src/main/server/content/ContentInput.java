package main.server.content;

public class ContentInput extends ContentItem {
  
  public ContentInput(int x, int y, int captionWidth, int inputWidth, String caption, String valueName, String value) {
    super("input");
    setAttribute("x", x);
    setAttribute("y", y);
    setAttribute("captionWidth", captionWidth);
    setAttribute("inputWidth", inputWidth);
    setAttribute("caption", caption);
    setAttribute("name", valueName); 
    if(value != null)
      setAttribute("value", value);
  }
  
}
