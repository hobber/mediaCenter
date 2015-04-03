package main.server.content;

import org.json.JSONException;

public class ContentText extends ContentItem {
	
	public enum TextType {
		NORMAL, TITLE, BLOCK
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
			put("type", "text");
			put("x", x);
			put("y", y);		
			put("text", text);
			if(link != null && link.length() > 0)
				put("url", link);

			if(type == TextType.TITLE)
				put("style", "font-size: 24px; font-weight: 900;");
			else if(type == TextType.BLOCK)
				put("style", "text-align: justify; margin-right: 50px;");
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
