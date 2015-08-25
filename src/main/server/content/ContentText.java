package main.server.content;


public class ContentText extends ContentItem {
  
	public enum TextType {
		NORMAL, TITLE, SUBTITLE, BLOCK, FIXED_SIZE
	}

	public ContentText(int x, int y, String text) {
	  super("text");
		create(x, y, text, "", TextType.NORMAL);
	}
	
	public ContentText(int x, int y, String text, String link) {
	  super("text");
		create(x, y, text, link, TextType.NORMAL);
	}
	
	public ContentText(int x, int y, String text, TextType type) {
	  super("text");
		create(x, y, text, "", type);
	}
	
	private void create(int x, int y, String text, String link, TextType type) {
		setAttribute("x", x);
		setAttribute("y", y);		
		setAttribute("text", text);
		
		if(link != null && link.length() > 0)
		  setAttribute("url", link);

		if(type == TextType.TITLE)
		  setAttribute("style", "font-size: 24px; font-weight: 900;");
		else if(type == TextType.SUBTITLE)
		  setAttribute("style", "font-size: 21px; font-weight: 700;");
		else if(type == TextType.BLOCK)
		  setAttribute("style", "text-align: justify; margin-right: 50px;");
		else if(type == TextType.FIXED_SIZE)
		  setAttribute("style", "font-family:'Courier New', Arial;");
	}
	
	public void setSelectionId(String id) {
	  setAttribute("selectionId", id);
	}
}
