package main.server.content;

import org.json.JSONException;

public class ContentText extends ContentItem {
  
	public enum TextType {
		NORMAL, TITLE, SUBTITLE, BLOCK
	}

	public ContentText(int x, int y, String text) {
		create(x, y, text, "", TextType.NORMAL);
	}
	
	public ContentText(int x, int y, String text, String link) {
		create(x, y, text, link, TextType.NORMAL);
	}
	
	public ContentText(int x, int y, String text, TextType type) {
		create(x, y, text, "", type);
	}
	
	private void create(int x, int y, String text, String link, TextType type) {
		try {
			data.put("type", "text");
			data.put("x", x);
			data.put("y", y);		
			data.put("text", text);
			if(link != null && link.length() > 0)
			  data.put("url", link);

			if(type == TextType.TITLE)
			  data.put("style", "font-size: 24px; font-weight: 900;");
			else if(type == TextType.SUBTITLE)
			  data.put("style", "font-size: 21px; font-weight: 700;");
			else if(type == TextType.BLOCK)
			  data.put("style", "text-align: justify; margin-right: 50px;");
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
