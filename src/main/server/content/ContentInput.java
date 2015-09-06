package main.server.content;

public class ContentInput extends ContentItem {
  
  public ContentInput(int x, int y, int captionWidth, int inputWidth, String caption, String valueName) {
    super("input");
    create(x, y, captionWidth, inputWidth, caption, valueName, null, null);
  }
  
  public ContentInput(int x, int y, int captionWidth, int inputWidth, String caption, String valueName, String value) {
    super("input");
    create(x, y, captionWidth, inputWidth, caption, valueName, value, null);
  }
  
  public ContentInput(int x, int y, int captionWidth, int inputWidth, String caption, String valueName, String value, String error) {
    super("input");
    create(x, y, captionWidth, inputWidth, caption, valueName, value, error);
  }
  
  private void create(int x, int y, int captionWidth, int inputWidth, String caption, String valueName, String value, String error) {
    
    setAttribute("x", x);
    setAttribute("y", y);
    setAttribute("captionWidth", captionWidth);
    setAttribute("inputWidth", inputWidth);
    setAttribute("caption", caption);
    setAttribute("name", valueName); 
    if(value != null && value.length() > 0)
      setAttribute("value", value);
    if(error != null && error.length() > 0)
      setAttribute("error", error);
  }  
}
