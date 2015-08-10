package main.server.content;


public class ContentInputForm extends ContentItem {

  private class ContentTextInput extends ContentItem {
    
    public ContentTextInput(String caption, String valueName) {
      super("textInput");
      setAttribute("caption", caption);
      setAttribute("name", valueName); 
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
     items.add(new ContentTextInput(caption, valueName));
  }
}
