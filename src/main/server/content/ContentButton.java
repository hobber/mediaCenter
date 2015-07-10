package main.server.content;

import org.json.JSONException;

public class ContentButton extends ContentItem {
  
  public ContentButton(int x, int y, String text, String callbackParameter) {
    try {
      data.put("type", "button");
      data.put("x", x);
      data.put("y", y);   
      data.put("text", text);
      data.put("parameter", callbackParameter);
    } catch(JSONException e) {
      System.err.println("ERROR: " + e.getMessage());
    }
  }

}
