package main.server.content;


public class ContentTextLine extends ContentItem {
  
  private ContentItemList text = new ContentItemList();
  
  public ContentTextLine(int x, int y) {
    super("textline");
    setAttribute("x", x);
    setAttribute("y", y);
    setAttribute("text", text);
  }

  public ContentTextLine addFixedWidthText(String text) {
    this.text.add(new ContentGenericItem("span", text)
      .setAttribute("style", "font-family:'Courier New', Arial;")
      .setAttribute("fullWidth", "true"));
    return this;
  }
  
  public ContentTextLine addFixedWidthText(String text, ContentEventOnClick onClick) {
    this.text.add(new ContentGenericItem("span", text)
      .setAttribute("style", "font-family:'Courier New', Arial;")
      .setAttribute("fullWidth", "true")
      .setOnClick(onClick));
    return this;
  }
  
  public ContentTextLine addFixedWidthTextBold(String text) {
    this.text.add(new ContentGenericItem("b", text)
      .setAttribute("style", "font-family:'Courier New', Arial;")
      .setAttribute("fullWidth", "true"));
    return this;
  }
  
  public ContentTextLine addFixedWidthTextBold(String text, ContentEventOnClick onClick) {
    this.text.add(new ContentGenericItem("b", text)
    .setAttribute("style", "font-family:'Courier New', Arial;")
    .setAttribute("fullWidth", "true")
    .setOnClick(onClick));
    return this;
}
}
