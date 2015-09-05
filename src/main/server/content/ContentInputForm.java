package main.server.content;


public class ContentInputForm extends ContentItem {

  private class ContentTextInput extends ContentItem {
    
    public ContentTextInput(String caption, String valueName, String value) {
      super("input");
      setAttribute("caption", caption);
      setAttribute("name", valueName); 
      if(value != null)
        setAttribute("value", value);
    }
  }
  
  private ContentItemList items = new ContentItemList();
  
  public ContentInputForm(String parameter, String buttonCaption) {
    super("inputForm");
    setAttribute("parameter", parameter);
    setAttribute("buttonCaption", buttonCaption);
    setAttribute("items", items);
  }
  
  public void addInput(String caption, String valueName) {
    items.add(new ContentTextInput(caption, valueName, null));
  }
  
  public void addInput(String caption, String valueName, String value) {
     items.add(new ContentTextInput(caption, valueName, value));
  }
}
