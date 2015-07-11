package main.server.content;

import org.json.JSONObject;

public abstract class ContentItem {
  
  protected JSONObject data = new JSONObject();
  protected ContentOptions options;
  
  public void setOptions(ContentOptions options) {
    this.options = options;
  }
	
  public String getContentString() {
    String s = data.toString();
    if(options != null)
      s = s.substring(0, s.length() - 1) + ", \"options\": " + options + "}";
    return s;
  }
}
